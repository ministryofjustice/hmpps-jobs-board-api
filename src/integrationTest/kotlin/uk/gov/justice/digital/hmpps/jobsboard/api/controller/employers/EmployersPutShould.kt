package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.sainsburys
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.tesco

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

  @Test
  fun `update an existing Employer`() {
    val employerId = assertAddEmployerIsCreated(employer = tesco)

    assertUpdateEmployerIsOk(
      employerId = employerId,
      body = sainsburys.requestBody,
    )

    assertGetEmployerIsOK(
      employerId = employerId,
      expectedResponse = sainsburys.requestBody,
    )
  }
}
