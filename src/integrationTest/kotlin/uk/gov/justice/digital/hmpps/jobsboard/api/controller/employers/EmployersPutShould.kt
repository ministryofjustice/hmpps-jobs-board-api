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
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerEventType
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.hmpps.sqs.countMessagesOnQueue
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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

      assertMessageHasBeenSent(
        expectedEventType = EmployerEventType.EMPLOYER_CREATED,
        expectedEmployerId = employer.id.id,
      )
    }
  }

  @Nested
  @DisplayName("Given an employer has been created")
  inner class GivenAnEmployer {
    private lateinit var employerId: String
    private lateinit var employer: Employer

    private val errorRespoonseInvalidSizeOfEmployerName: String by lazy {
      "name - size must be between 3 and 100".let { error ->
        """
            {"status":400,"errorCode":null,"userMessage":"Validation failure: $error","developerMessage":"$error","moreInfo":null}
        """.trimIndent()
      }
    }

    @BeforeEach
    fun setUp() {
      employerId = assertAddEmployerIsCreated(employer = sainsburys)
      employer = sainsburys.copy(id = EntityId(employerId))
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
    fun `not update an existing employer with validation error, as employer name is too long`() {
      val employerBuilder = EmployerMother.builder().from(sainsburys).apply { name = ".".repeat(100) }
      assertUpdateEmployerIsOk(employerId, employerBuilder.build().requestBody)

      employerBuilder.name += "x"
      assertUpdateEmployerThrowsValidationError(
        employerId,
        employerBuilder.build().requestBody,
        expectedResponse = errorRespoonseInvalidSizeOfEmployerName,
      )
    }

    @Test
    fun `not update an existing employer with validation error, as employer name is too short`() {
      val employerBuilder = EmployerMother.builder().from(sainsburys).apply { name = ".".repeat(3) }
      assertUpdateEmployerIsOk(employerId, employerBuilder.build().requestBody)

      employerBuilder.run { name = name.substring(0..1) }
      assertUpdateEmployerThrowsValidationError(
        employerId,
        employerBuilder.build().requestBody,
        expectedResponse = errorRespoonseInvalidSizeOfEmployerName,
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

      assertMessageHasBeenSent(
        expectedEventType = EmployerEventType.EMPLOYER_UPDATED,
        expectedEmployerId = employer.id.id,
        expectedCount = 2,
      )
    }

    @Nested
    inner class AndAnotherEmployer {
      private val anotherEmployer = tesco.copy(id = EntityId(randomUUID()))
      private val duplicateEmployer: Employer get() = anotherEmployer.copy(name = employer.name)

      @Test
      fun `NOT create another employer with duplicate name`() {
        val expectedError = duplicateEmployerErrorResponse(defaultCurrentTimeLocal)

        duplicateEmployer.run { assertAddEmployerThrowsValidationError(id.id, requestBody, expectedError) }
      }

      @Test
      fun `NOT update another employer to duplicate name (trimmed)`() {
        assertAddEmployerIsCreated(anotherEmployer)
        val expectedError = duplicateEmployerErrorResponse(defaultCurrentTimeLocal)

        duplicateEmployer.copy(name = "   ${duplicateEmployer.name}   ")
          .run { assertAddEmployerThrowsValidationError(id.id, requestBody, expectedError) }
      }
    }
  }

  private fun assertMessageHasBeenSent(
    expectedEventType: EmployerEventType,
    expectedEmployerId: String,
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
      assertThat(messageBody["employerId"].textValue()).isEqualTo(expectedEmployerId)
    }
  }

  private fun duplicateEmployerErrorResponse(timestamp: LocalDateTime) = duplicateEmployerErrorResponse(timestamp.atZone(ZoneId.systemDefault()).toInstant())

  private fun duplicateEmployerErrorResponse(timestamp: Instant): String {
    val userMessage = "Validation failed"
    val errorMessage = "Bad request: Duplicate Employer"
    val errorDetails = """
      {"field":"name","message":"The name provided already exists. Please choose a different name.","code":"DUPLICATE_EMPLOYER"}
    """.trimIndent()
    return """
      {
        "status": 400,
        "userMessage":"$userMessage",
        "timestamp":"$timestamp",
        "error": "$errorMessage", 
        "details": [$errorDetails]
       }
    """.trimIndent()
  }
}
