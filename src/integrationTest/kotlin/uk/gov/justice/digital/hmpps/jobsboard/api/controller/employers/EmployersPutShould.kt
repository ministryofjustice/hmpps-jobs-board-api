package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.junit.jupiter.api.Test

class EmployersPutShould : EmployerTestCase() {
  @Test
  fun `create a valid Employer`() {
    assertAddEmployerIsOk(body = sainsburysBody)
  }

  @Test
  fun `not create an Employer with invalid UUID`() {
    assertAddEmployerThrowsValidationError(
      employerId = "invalid-uuid",
      body = tescoBody,
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
    assertAddEmployerIsOk(body = tescoBody)
    val uuid = assertAddEmployerIsOk(body = sainsburysBody)

    assertGetEmployerIsOK(
      employerId = uuid,
      expectedResponse = sainsburysBody,
    )
  }
}
