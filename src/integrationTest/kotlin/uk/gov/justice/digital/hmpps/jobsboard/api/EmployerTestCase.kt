package uk.gov.justice.digital.hmpps.jobsboard.api

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import java.util.UUID.randomUUID

private const val EMPLOYERS_ENDPOINT = "/employers"

class EmployerTestCase : ApplicationTestCase() {
  protected fun assertAddEmployerIsOk(
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
      employerId = employerId,
      body = body,
      expectedStatus = BAD_REQUEST,
      expectedResponse = expectedResponse,
    )
  }

  private fun assertAddEmployer(
    employerId: String? = null,
    body: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
  ): String {
    val finalEmployerId = employerId ?: randomUUID().toString()
    assertRequestWithBody(
      url = "$EMPLOYERS_ENDPOINT/$finalEmployerId",
      body = body,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
    return finalEmployerId
  }

  protected fun assertGetEmployerIsOK(
    userId: String? = null,
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = EMPLOYERS_ENDPOINT
    userId?.let { url = "$url/$it" }
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

  private fun newEmployerBody(name: String, description: String, sector: String, status: String): String = """
        {
          "name": "$name",
          "description": "$description",
          "sector": "$sector",
          "status": "$status"
        }
  """.trimIndent()

  val tescoBody: String = newEmployerBody(
    name = "Tesco",
    description = "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
    sector = "RETAIL",
    status = "SILVER",
  )

  val tescoLogisticsBody: String = newEmployerBody(
    name = "Tesco",
    description = "This is another Tesco employer that provides logistic services.",
    sector = "LOGISTICS",
    status = "GOLD",
  )

  val sainsburysBody: String = newEmployerBody(
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century.",
    sector = "RETAIL",
    status = "GOLD",
  )

  val amazonBody: String = newEmployerBody(
    name = "Amazon",
    description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
    sector = "LOGISTICS",
    status = "KEY_PARTNER",
  )
}
