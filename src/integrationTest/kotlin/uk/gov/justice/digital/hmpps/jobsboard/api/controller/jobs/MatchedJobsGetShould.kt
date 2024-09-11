package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test

class MatchedJobsGetShould : MatchedJobsTestCase() {
  @Test
  fun `retrieve a default paginated empty candidate matching Jobs list`() {
    assertGetMatchedJobsIsOK(
      expectedResponse = expectedResponseListOf(),
    )
  }
}
