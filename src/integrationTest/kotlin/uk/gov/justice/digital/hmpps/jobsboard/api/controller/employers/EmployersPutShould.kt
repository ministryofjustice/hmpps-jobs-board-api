package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.responseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.sainsburys
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco

class EmployersPutShould : EmployerTestCase() {
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
  }
}
