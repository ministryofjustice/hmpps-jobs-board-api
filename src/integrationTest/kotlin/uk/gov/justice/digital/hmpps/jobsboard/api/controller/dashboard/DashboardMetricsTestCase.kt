package uk.gov.justice.digital.hmpps.jobsboard.api.controller.dashboard

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import java.time.LocalDate

const val METRICS_SUMMARY_ENDPOINT = "$DASHBOARD_ENDPOINT/summary"
const val METRICS_APPLICATIONS_STAGE_ENDPOINT = "$DASHBOARD_ENDPOINT/applications-stage"

@Transactional(propagation = Propagation.NOT_SUPPORTED)
abstract class DashboardMetricsTestCase(
  val endpoint: String,
) : DashboardTestCase() {

  protected fun assertGetMetricsIsOk(
    prisonId: String,
    dateFrom: LocalDate,
    dateTo: LocalDate,
    expectedResponse: String? = null,
  ) = assertGetMetricsIsOk(
    parameters = "prisonId=$prisonId&dateFrom=$dateFrom&dateTo=$dateTo",
    expectedResponse = expectedResponse,
  )

  protected fun assertGetMetricsIsOk(
    parameters: String,
    expectedResponse: String? = null,
  ) = assertGetMetricsIsExpected(parameters, HttpStatus.OK, expectedResponse)

  protected fun assertGetMetricsReturnsBadRequestError(
    parameters: String? = null,
    expectedResponse: String? = null,
  ) = assertGetMetricsIsExpected(parameters, HttpStatus.BAD_REQUEST, expectedResponse)

  protected fun expectedErrorMessageMissingParameter(paramName: String, paramType: String = "String") =
    expectedErrorMessage(
      errorMessage = "Required request parameter '$paramName' for method parameter type $paramType is not present",
      userMessagePrefix = "Missing required parameter",
    )

  protected fun expectedErrorMessageMissingDateParameter(paramName: String) =
    expectedErrorMessageMissingParameter(paramName, "LocalDate")

  protected fun expectedErrorMessageParameterTypeMismatch(paramName: String, paramValue: Any) =
    expectedErrorMessageValidationFailure(
      errorMessage = "Type mismatch: parameter '$paramName' with value '$paramValue'",
    )

  protected fun expectedErrorMessageInvalidDatePeriod(dateFrom: String, dateTo: String) =
    expectedErrorMessageValidationFailure(
      errorMessage = "dateFrom ($dateFrom) cannot be after dateTo ($dateTo)",
    )

  private fun expectedErrorMessageValidationFailure(errorMessage: String) =
    expectedErrorMessage(errorMessage, userMessagePrefix = "Validation failure")

  private fun expectedErrorMessage(errorMessage: String, userMessagePrefix: String? = null) = """
    {"status":400,"errorCode":null,"userMessage":"${userMessagePrefix?.let { "$it: " }}$errorMessage","developerMessage":"$errorMessage","moreInfo":null}
  """.trimIndent()

  private fun assertGetMetricsIsExpected(
    parameters: String? = null,
    expectedStatus: HttpStatus = HttpStatus.OK,
    expectedResponse: String? = null,
  ) {
    assertRequestWithoutBody(
      url = "$endpoint${parameters?.let { "?$it" } ?: ""}",
      expectedHttpVerb = HttpMethod.GET,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
  }
}

abstract class MetricsSummaryTestCase : DashboardMetricsTestCase(METRICS_SUMMARY_ENDPOINT) {
  protected final fun metricSummaryResponse(numberOfApplicants: Int, numberOfJobs: Int) = """
    {
      "numberOfApplicants": $numberOfApplicants,
      "numberOfJobs": $numberOfJobs
    }
  """.trimIndent()
}

abstract class MetricsApplicationsTestCase(endpoint: String) : DashboardMetricsTestCase(endpoint) {
  protected final val Map<ApplicationStatus, Long>.metricsResponses: String get() = toMetricsResponses(this)

  private fun toMetricsResponses(expectedMetrics: Map<ApplicationStatus, Long>) = expectedMetrics.map {
    """
      {
        "applicationStatus":"${it.key}",
        "numberOfApplications":${it.value}
      }
    """.trimIndent()
  }.joinToString(separator = ",").let { "[$it]" }
}

abstract class MetricsTotalApplicationsStageTestCase : MetricsApplicationsTestCase(METRICS_APPLICATIONS_STAGE_ENDPOINT)
