package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.requestBody

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
  fun `update an existing Job`() {
    val employerId = "bf392249-b360-4e3e-81a0-8497047987e8"
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
}
