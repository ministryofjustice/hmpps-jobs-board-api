package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.ApplicationTestCase

const val EMPLOYERS_ENDPOINT = "/employers"

class EmployerTestCase : ApplicationTestCase() {
  protected fun assertAddEmployerIsCreated(
    body: String,
  ): String {
    return assertAddEmployer(
      body = body,
      expectedStatus = CREATED,
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

  protected fun expectedResponseListOf(vararg elements: String): String {
    return expectedResponseListOf(10, 0, elements = elements)
  }

  protected fun expectedResponseListOf(size: Int, page: Int, vararg elements: String): String {
    return expectedResponseListOf(size, page, elements.size, *elements)
  }

  protected fun expectedResponseListOf(size: Int, page: Int, totalElements: Int, vararg elements: String): String {
    val totalPages = (totalElements + size - 1) / size
    val expectedResponse = """
         {
          "content": [ ${elements.joinToString(separator = ",")}],
          "page": {
            "size": $size,
            "number": $page,
            "totalElements": $totalElements,
            "totalPages": $totalPages
          }
        }
    """.trimIndent()
    return expectedResponse
  }
}
