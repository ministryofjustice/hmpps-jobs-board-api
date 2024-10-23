package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK

const val APPLICATION_HISTORIES_ENDPOINT = "$APPLICATIONS_ENDPOINT/histories"

abstract class ApplicationHistoriesTestCase : ApplicationsTestCase() {

  protected fun assertGetApplicationHistoriesIsOk(
    parameters: String,
    expectedResponse: String? = null,
  ) = assertGetApplicationHistories(parameters, OK, expectedResponse)

  protected fun assertGetApplicationHistoriesReturnsBadRequestError(
    parameters: String? = null,
    expectedResponse: String? = null,
  ) = assertGetApplicationHistories(parameters, BAD_REQUEST, expectedResponse)

  private fun assertGetApplicationHistories(
    parameters: String? = null,
    expectedStatus: HttpStatus = OK,
    expectedResponse: String? = null,
  ) {
    assertRequestWithoutBody(
      url = "$APPLICATION_HISTORIES_ENDPOINT${parameters?.let { "?$it" } ?: ""}",
      expectedHttpVerb = GET,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
  }
}
