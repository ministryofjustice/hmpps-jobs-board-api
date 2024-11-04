package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK

const val MATCHED_JOBS_CLOSING_SOON_ENDPOINT = "${MATCHING_CANDIDATE_ENDPOINT}/closing-soon"

class MatchedJobClosingSoonTestCase : JobsTestCase() {
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

  protected fun assertGetMatchedJobClosingSoonReturnsBadRequestError(
    expectedResponse: String? = null,
  ) {
    assertRequestWithoutBody(
      url = MATCHED_JOBS_CLOSING_SOON_ENDPOINT,
      expectedStatus = BAD_REQUEST,
      expectedHttpVerb = GET,
      expectedResponse = expectedResponse,
    )
  }
}
