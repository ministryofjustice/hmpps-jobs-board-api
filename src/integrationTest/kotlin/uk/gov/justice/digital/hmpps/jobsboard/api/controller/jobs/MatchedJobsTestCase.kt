package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpStatus.OK

const val MATCHING_CANDIDATE_ENDPOINT = "${JOBS_ENDPOINT}/matching-candidate"

abstract class MatchedJobsTestCase : JobsTestCase() {

  protected fun assertGetMatchedJobsIsOK(expectedResponse: String) {
    assertResponse(
      url = MATCHING_CANDIDATE_ENDPOINT,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }
}
