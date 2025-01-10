package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.responseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.sainsburys
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tescoLogistics
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerEventType
import uk.gov.justice.hmpps.sqs.countMessagesOnQueue

class EmployersPutShould : EmployerTestCase() {

  @Nested
  @DisplayName("Given employer has not been created yet.")
  inner class GivenEmployerNotCreatedYet {
    @Test
    fun `create a valid Employer`() {
      assertAddEmployerIsCreated(employer = sainsburys)
    }

    @Test
    fun `not create an Employer with invalid UUID`() {
      assertAddEmployerThrowsValidationError(
        employerId = "invalid-uuid",
        body = tesco.requestBody,
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
    fun `send event after employer created`() {
      val employer = tescoLogistics
      assertAddEmployerIsCreated(employer)

      await untilCallTo { outboundSqsClientSpy.countMessagesOnQueue(outboundQueueUrl).get() } matches { it == 1 }
      val messageCaptor = argumentCaptor<SendMessageRequest>()
        .also { verify(outboundSqsClientSpy).sendMessage(it.capture()) }

      assertMessageIsExpected(
        actualMessage = messageCaptor.firstValue,
        expectedEventType = EmployerEventType.EMPLOYER_CREATED,
        expectedEmployerId = employer.id.id,
      )
    }
  }

  @Nested
  @DisplayName("Given an employer has been created")
  inner class GivenAnEmployer {
    private lateinit var employerId: String

    @BeforeEach
    fun setUp() {
      employerId = assertAddEmployerIsCreated(employer = sainsburys)
    }

    @Test
    fun `update an existing Employer`() {
      assertUpdateEmployerIsOk(
        employerId = employerId,
        body = sainsburys.requestBody,
      )

      assertGetEmployerIsOK(
        employerId = employerId,
        expectedResponse = sainsburys.responseBody,
      )
    }

    @Test
    fun `not update an existing employer with validation error, as employer description is too long`() {
      val employerBuilder = EmployerMother.builder().from(sainsburys).apply { description = ".".repeat(1000) }
      assertUpdateEmployerIsOk(
        employerId = employerId,
        body = employerBuilder.build().requestBody,
      )

      employerBuilder.description += "x"

      val expectedError = "description - size must be between 0 and 1000".let { error ->
        """
        {"status":400,"errorCode":null,"userMessage":"Validation failure: $error","developerMessage":"$error","moreInfo":null}
        """.trimIndent()
      }
      assertUpdateEmployerThrowsValidationError(employerId, employerBuilder.build().requestBody, expectedError)
    }

    @Test
    fun `send event after employer updated`() {
      val employer = sainsburys
      assertUpdateEmployerIsOk(employerId = employer.id.id, body = employer.requestBody)

      await untilCallTo { outboundSqsClientSpy.countMessagesOnQueue(outboundQueueUrl).get() } matches { it == 2 }
      val messageCaptor = argumentCaptor<SendMessageRequest>()
        .also { verify(outboundSqsClientSpy, times(2)).sendMessage(it.capture()) }

      assertMessageIsExpected(
        actualMessage = messageCaptor.secondValue,
        expectedEventType = EmployerEventType.EMPLOYER_UPDATED,
        expectedEmployerId = employer.id.id,
      )
    }
  }

  private fun assertMessageIsExpected(
    actualMessage: SendMessageRequest,
    expectedEventType: EmployerEventType,
    expectedEmployerId: String,
  ) {
    actualMessage.messageAttributes()["eventType"]!!.stringValue().let { eventTypeMessageAttribute ->
      assertThat(eventTypeMessageAttribute).isEqualTo(expectedEventType.type)
    }
    objectMapper.readTree(actualMessage.messageBody()).let { messageBody ->
      assertThat(messageBody["eventType"].textValue()).isEqualTo(expectedEventType.eventTypeCode)
      assertThat(messageBody["employerId"].textValue()).isEqualTo(expectedEmployerId)
    }
  }
}
