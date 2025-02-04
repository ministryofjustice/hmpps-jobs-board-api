package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobEventType
import uk.gov.justice.hmpps.sqs.countMessagesOnQueue

class JobsPutShould : JobsTestCase() {
  @Test
  fun `create a valid Job`() {
    assertAddEmployerIsCreated(employer = amazon)
    assertAddJobIsCreated(job = amazonForkliftOperator)
  }

  @Test
  fun `create a valid Job with empty optional attributes`() {
    assertAddEmployerIsCreated(employer = abcConstruction)
    assertAddJobIsCreated(job = abcConstructionApprentice)
  }

  @Test
  fun `not create a Job with invalid UUID`() {
    assertAddJobThrowsValidationError(
      jobId = "invalid-uuid",
      body = amazonForkliftOperator.requestBody,
      expectedResponse = """
        {
          "status":400,
          "errorCode":null,
          "userMessage":"Validation failure: createOrUpdate.id: Invalid UUID format",
          "developerMessage":"createOrUpdate.id: Invalid UUID format",
          "moreInfo":null
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `send event after job created`() {
    val job = tescoWarehouseHandler
    assertAddEmployerIsCreated(job.employer)
    assertAddJobIsCreated(job)

    // first message is employer created, second message shall be job created.
    assertMessageHasBeenSent(
      expectedEventType = JobEventType.JOB_CREATED,
      expectedJobId = job.id.id,
      expectedCount = 2,
    )
  }

  @Test
  fun `update an existing Job`() {
    assertAddEmployerIsCreated(employer = amazon)

    val jobId = assertAddJobIsCreated(job = amazonForkliftOperator)

    assertUpdateJobIsOk(
      jobId = jobId,
      body = amazonForkliftOperator.requestBody,
    )

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = amazonForkliftOperator.requestBody,
    )
  }

  @Test
  fun `not update an existing job with validation error, as offence exclusions details is too long`() {
    assertAddEmployerIsCreated(employer = amazon)

    val jobBuilder = JobMother.builder().from(amazonForkliftOperator).apply {
      offenceExclusionsDetails = ".".repeat(500)
    }
    val jobId = assertAddJobIsCreated(job = jobBuilder.build()).also {
      jobBuilder.id = jobBuilder.id.copy(id = it)
    }

    jobBuilder.offenceExclusionsDetails += "x"
    val expectedError = "offenceExclusionsDetails - size must be between 0 and 500".let { error ->
      """
        {"status":400,"errorCode":null,"userMessage":"Validation failure: $error","developerMessage":"$error","moreInfo":null}
      """.trimIndent()
    }
    assertAddJobThrowsValidationError(jobId, jobBuilder.build().requestBody, expectedError)
  }

  @Test
  fun `update an existing Employer maintaining its related Jobs`() {
    val employerId = assertAddEmployerIsCreated(employer = amazon)
    val jobId = assertAddJobIsCreated(job = amazonForkliftOperator)

    val updatedAmazon = builder().from(amazon).withName("${amazon.name} updated").build()

    assertUpdateEmployerIsOk(
      employerId = employerId,
      body = updatedAmazon.requestBody,
    )

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = amazonForkliftOperator.requestBody,
    )
  }

  @Test
  fun `send event after job updated`() {
    val job = tescoWarehouseHandler
    assertAddEmployerIsCreated(job.employer)
    assertAddJobIsCreated(job)

    assertUpdateJobIsOk(jobId = job.id.id, body = job.requestBody)

    // first message is employer created, second message is job created. third message shall be job updated.
    assertMessageHasBeenSent(
      expectedEventType = JobEventType.JOB_UPDATED,
      expectedJobId = job.id.id,
      expectedCount = 3,
    )
  }

  private fun assertMessageHasBeenSent(
    expectedEventType: JobEventType,
    expectedJobId: String,
    expectedCount: Int = 1,
  ) {
    await untilCallTo {
      outboundSqsClientSpy.countMessagesOnQueue(outboundQueueUrl).get()
    } matches { it == expectedCount }

    val actualMessage = argumentCaptor<SendMessageRequest>()
      .also { verify(outboundSqsClientSpy, times(expectedCount)).sendMessage(it.capture()) }
      .lastValue

    actualMessage.messageAttributes()["eventType"]!!.stringValue().let { eventTypeMessageAttribute ->
      assertThat(eventTypeMessageAttribute).isEqualTo(expectedEventType.type)
    }
    objectMapper.readTree(actualMessage.messageBody()).let { messageBody ->
      assertThat(messageBody["eventType"].textValue()).isEqualTo(expectedEventType.eventTypeCode)
      assertThat(messageBody["jobId"].textValue()).isEqualTo(expectedJobId)
    }
  }
}
