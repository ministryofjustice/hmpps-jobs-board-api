package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpStatus.OK

abstract class MatchingCandidateJobDetailsTestCase : JobsTestCase() {

  protected fun assertGetMatchingCandidateJobDetailsIsOK(
    jobId: String,
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = "$JOBS_ENDPOINT/$jobId/$MATCHING_CANDIDATE_PATH_SUFFIX"
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }
}

const val MATCHING_CANDIDATE_PATH_SUFFIX = "matching-candidate"
