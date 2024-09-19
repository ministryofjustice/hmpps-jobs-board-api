package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.abcConstructionBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.amazonBody
import java.util.*

class JobsPutShould : JobsTestCase() {
  @Test
  fun `create a valid Job`() {
    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )
    assertAddJobIsCreated(body = amazonForkliftOperatorJobBody)
  }

  @Test
  fun `create a valid Job with empty optional attributes`() {
    assertAddEmployer(
      id = "182e9a24-6edb-48a6-a84f-b7061f004a97",
      body = abcConstructionBody,
      expectedStatus = CREATED,
    )
    assertAddJobIsCreated(body = abcConstructionJobBody)
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
          "userMessage":"Validation failure: createOrUpdate.id: Invalid UUID format",
          "developerMessage":"createOrUpdate.id: Invalid UUID format",
          "moreInfo":null
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `update an existing Job`() {
    val employerId = "bf392249-b360-4e3e-81a0-8497047987e8"
    assertAddEmployer(
      id = employerId,
      body = amazonBody,
      expectedStatus = CREATED,
    )

    val jobId = assertAddJobIsCreated(body = amazonForkliftOperatorJobBody)

    assertUpdateJobIsOk(
      jobId = jobId,
      body = amazonForkliftOperatorJobBody,
    )

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = amazonForkliftOperatorJobBody,
    )
  }
}
