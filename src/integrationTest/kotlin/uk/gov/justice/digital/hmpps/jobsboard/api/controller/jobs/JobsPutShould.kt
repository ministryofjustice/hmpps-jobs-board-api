package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED

class JobsPutShould : JobsTestCase() {
  @Test
  fun `create a valid Job`() {
    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )
    assertAddJobIsOk(body = amazonForkliftOperatorJobBody)
  }

  @Test
  fun `not create a Job with invalid UUID`() {
    assertAddJobThrowsValidationError(
      jobId = "invalid-uuid",
      body = amazonForkliftOperatorJobBody,
      expectedResponse = """
        {
          "status":400,
          "errorCode":null,
          "userMessage":"Validation failure: create.id: Invalid UUID format",
          "developerMessage":"create.id: Invalid UUID format",
          "moreInfo":null
        }
      """.trimIndent(),
    )
  }

  // TODO: Should not create a Job when the Employer doesn't exist
}