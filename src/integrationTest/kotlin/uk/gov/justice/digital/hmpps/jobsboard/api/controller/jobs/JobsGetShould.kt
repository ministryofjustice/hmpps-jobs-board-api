package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED

class JobsGetShould : JobsTestCase() {
  @Test
  fun `retrieve an existing Job`() {
    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )
    val jobId = assertAddJobIsCreated(body = amazonForkliftOperatorJobBody)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = amazonForkliftOperatorJobBody,
    )
  }
}
