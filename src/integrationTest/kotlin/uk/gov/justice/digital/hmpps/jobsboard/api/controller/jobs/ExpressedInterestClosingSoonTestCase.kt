package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

const val EXPRESSED_INTEREST_CLOSING_SOON_ENDPOINT = "${JOBS_ENDPOINT}/expressed-interest/closing-soon"

class ExpressedInterestClosingSoonTestCase : JobsTestCase() {
  protected fun assertGetExpressedInterestClosingSoonIsOk(
    parameters: String? = null,
    expectedResponse: String? = null,
  ) {
    var url = EXPRESSED_INTEREST_CLOSING_SOON_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetExpressedInterestClosingSoonReturnsBadRequestError(
    expectedResponse: String? = null,
  ) {
    assertRequestWithoutBody(
      url = EXPRESSED_INTEREST_CLOSING_SOON_ENDPOINT,
      expectedStatus = BAD_REQUEST,
      expectedHttpVerb = GET,
      expectedResponse = expectedResponse,
    )
  }

  protected fun expressInterestToJobs(prisonNumber: String, vararg jobs: Job) = jobs.forEach { job ->
    assertAddExpressionOfInterest(job.id.id, prisonNumber)
  }

  protected fun archiveJobs(prisonNumber: String, vararg jobs: Job) = jobs.forEach { job ->
    assertAddArchived(job.id.id, prisonNumber)
  }
}
