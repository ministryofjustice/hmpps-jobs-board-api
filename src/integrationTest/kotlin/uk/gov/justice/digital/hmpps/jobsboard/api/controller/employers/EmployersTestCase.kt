package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.ApplicationTestCase
import java.time.Instant

const val EMPLOYERS_ENDPOINT = "/employers"

class EmployerTestCase : ApplicationTestCase() {
  val employerCreationTime = Instant.parse("2024-07-01T01:00:00Z")

  protected fun assertAddEmployerIsCreated(
    body: String,
  ): String {
    return assertAddEmployer(
      body = body,
      expectedStatus = CREATED,
    )
  }

  protected fun assertUpdateEmployerIsOk(
    employerId: String,
    body: String,
  ): String {
    return assertAddEmployer(
      id = employerId,
      body = body,
      expectedStatus = OK,
    )
  }

  protected fun assertAddEmployerThrowsValidationError(
    employerId: String? = null,
    body: String,
    expectedResponse: String,
  ) {
    assertAddEmployer(
      id = employerId,
      body = body,
      expectedStatus = BAD_REQUEST,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetEmployerIsOK(
    employerId: String? = null,
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = EMPLOYERS_ENDPOINT
    employerId?.let { url = "$url/$it" }
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetEmployersIsOKAndSortedByName(
    parameters: String? = "",
    expectedNamesSorted: List<String>,
  ) {
    assertResponse(
      url = "$EMPLOYERS_ENDPOINT?$parameters",
      expectedStatus = OK,
      expectedNameSortedList = expectedNamesSorted,
    )
  }

  protected fun assertGetEmployersIsOkAndSortedByDate(
    parameters: String,
    expectedDatesSorted: List<String>,
  ) {
    assertResponse(
      url = "$EMPLOYERS_ENDPOINT?$parameters",
      expectedStatus = OK,
      expectedDateSortedList = expectedDatesSorted,
    )
  }

  protected fun assertGetEmployersIsOkAndSortedByDate(
    parameters: String,
    expectedSortingOrder: String = "asc",
  ) {
    assertResponse(
      url = "$EMPLOYERS_ENDPOINT?$parameters",
      expectedStatus = OK,
      expectedDateSortingOrder = expectedSortingOrder,
    )
  }
}
