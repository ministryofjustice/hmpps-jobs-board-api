package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

const val EXPRESSED_INTEREST_ENDPOINT = "${JOBS_ENDPOINT}/expressed-interest"

abstract class ExpressedInterestTestCase : JobsTestCase() {
  protected fun assertGetExpressedInterestIsOk(
    parameters: String? = null,
    expectedResponse: String? = null,
  ) {
    var url = EXPRESSED_INTEREST_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetExpressedInterestIsOKAndSortedByJobAndEmployer(
    parameters: String? = "",
    expectedJobTitlesSorted: List<String>,
    expectedEmployerNameSortedList: List<String>,
  ) {
    var url = EXPRESSED_INTEREST_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedJobTitleSortedList = expectedJobTitlesSorted,
      expectedEmployerNameSortedList = expectedEmployerNameSortedList,
    )
  }

  protected fun assertGetExpressedInterestIsOKAndSortedByClosingDate(
    parameters: String? = null,
    expectedSortingOrder: String = "asc",
  ) {
    var url = EXPRESSED_INTEREST_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedClosingDateSortingOrder = expectedSortingOrder,
    )
  }

  protected fun assertGetExpressedInterestIsOKAndSortedByLocation(
    parameters: String? = null,
    expectedDistanceSortedList: List<Double?>,
  ) {
    var url = EXPRESSED_INTEREST_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedDistanceSortedList = expectedDistanceSortedList,
    )
  }

  protected fun assertGetExpressedInterestReturnsBadRequestError(
    expectedResponse: String? = null,
  ) {
    assertRequestWithoutBody(
      url = EXPRESSED_INTEREST_ENDPOINT,
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

  override fun expectedResponseListOf(vararg elements: String): String = expectedResponseListOf(20, 0, elements = elements)
}
