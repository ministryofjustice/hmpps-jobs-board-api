package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application

const val OPEN_APPLICATIONS_ENDPOINT = "$APPLICATIONS_ENDPOINT/open"

abstract class ApplicationsByPrisonerTestCase(
  val endpoint: String,
) : ApplicationsTestCase() {
  protected fun assertGetApplicationsByPrisonerIsOk(
    parameters: String,
    expectedResponse: String? = null,
  ) = assertGetApplicationsByPrisoner(parameters, OK, expectedResponse)

  protected fun assertGetApplicationsByPrisonerReturnsBadRequestError(
    parameters: String? = null,
    expectedResponse: String? = null,
  ) = assertGetApplicationsByPrisoner(parameters, BAD_REQUEST, expectedResponse)

  private fun assertGetApplicationsByPrisoner(
    parameters: String? = null,
    expectedStatus: HttpStatus = OK,
    expectedResponse: String? = null,
  ) {
    assertRequestWithoutBody(
      url = "$endpoint${parameters?.let { "?$it" } ?: ""}",
      expectedHttpVerb = GET,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
  }

  protected val Application.responseBody get() = applicationResponseBody(this)

  private fun applicationResponseBody(application: Application): String {
    return application.let {
      """
      {
        "id": "${it.id}",
        "jobId": "${it.job.id}",
        "employerName": "${it.job.employer.name}",
        "jobTitle": "${it.job.title}",
        "applicationStatus": "${it.status}",
        "createdAt": "$jobCreationTime",
        "lastModifiedAt": "$jobCreationTime"
      }
      """.trimIndent()
    }
  }
}

abstract class OpenApplicationsTestCase : ApplicationsByPrisonerTestCase(OPEN_APPLICATIONS_ENDPOINT) {
  fun assertGetOpenApplicationsIsOk(
    parameters: String,
    expectedResponse: String? = null,
  ) = assertGetApplicationsByPrisonerIsOk(parameters, expectedResponse)

  fun assertGetOpenApplicationsReturnsBadRequestError(
    parameters: String? = null,
    expectedResponse: String? = null,
  ) = assertGetApplicationsByPrisonerReturnsBadRequestError(parameters, expectedResponse)
}
