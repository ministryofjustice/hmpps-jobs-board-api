package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK

const val MATCHING_CANDIDATE_PATH_SUFFIX = "matching-candidate"

abstract class MatchingCandidateJobDetailsTestCase : JobsTestCase() {

  protected fun assertGetMatchingCandidateJobDetailsIsOK(
    id: String,
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = "$JOBS_ENDPOINT/$id/$MATCHING_CANDIDATE_PATH_SUFFIX"
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetMatchingCandidateJobDetailsIsNotOK(
    jobId: String,
    parameters: String? = null,
    expectedStatus: HttpStatus,
  ) {
    var url = "$JOBS_ENDPOINT/$jobId/$MATCHING_CANDIDATE_PATH_SUFFIX"
    parameters?.let { url = "$url?$it" }
    assertRequestWithoutBody(
      url = url,
      expectedHttpVerb = HttpMethod.GET,
      expectedStatus = expectedStatus,
    )
  }
}
