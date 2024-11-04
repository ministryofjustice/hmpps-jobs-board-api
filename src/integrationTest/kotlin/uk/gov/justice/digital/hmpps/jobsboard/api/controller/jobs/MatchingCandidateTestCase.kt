package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK

const val MATCHING_CANDIDATE_ENDPOINT = "${JOBS_ENDPOINT}/matching-candidate"

abstract class MatchingCandidateTestCase : JobsTestCase() {

  protected fun assertGetMatchingCandidateJobsIsOK(
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = MATCHING_CANDIDATE_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
    parameters: String? = "",
    expectedJobTitlesSorted: List<String>,
  ) {
    var url = MATCHING_CANDIDATE_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedJobTitleSortedList = expectedJobTitlesSorted,
    )
  }

  protected fun assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
    parameters: String? = null,
    expectedSortingOrder: String = "asc",
  ) {
    var url = MATCHING_CANDIDATE_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedClosingDateSortingOrder = expectedSortingOrder,
    )
  }

  protected fun assertGetMatchingCandidateJobsReturnsBadRequestError(
    expectedResponse: String? = null,
  ) {
    assertRequestWithoutBody(
      url = MATCHING_CANDIDATE_ENDPOINT,
      expectedStatus = BAD_REQUEST,
      expectedHttpVerb = GET,
      expectedResponse = expectedResponse,
    )
  }
}
