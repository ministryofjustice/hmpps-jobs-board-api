package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpStatus.OK

const val MATCHED_JOBS_CLOSING_SOON_ENDPOINT = "${MATCHING_CANDIDATE_ENDPOINT}/closing-soon"

class MatchedJobClosingSoonTestCase : MatchingCandidateTestCase() {
  protected fun assertGetMatchedJobClosingSoonIsOk(
    parameters: String? = null,
    expectedResponse: String? = null,
  ) {
    var url = MATCHED_JOBS_CLOSING_SOON_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }
}
