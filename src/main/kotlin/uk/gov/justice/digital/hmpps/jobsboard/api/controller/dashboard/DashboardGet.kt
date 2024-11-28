package uk.gov.justice.digital.hmpps.jobsboard.api.controller.dashboard

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationMetricsRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.GetMetricsSummaryResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import java.time.LocalDate

@Validated
@RestController
@RequestMapping("/dashboard", produces = [APPLICATION_JSON_VALUE])
class DashboardGet(
  val applicationMetricsRetriever: ApplicationMetricsRetriever,
) {
  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/summary")
  @Operation(
    summary = "Retrieve metrics summary of given prison",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "The success status is set as the request has been processed correctly.",
      ),
      ApiResponse(
        responseCode = "400",
        description = "The failure status is set when the request is invalid. An error response will be provided.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Error: Unauthorised. The error status is set as the required authorisation was not provided.",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Error: Access Denied. The error status is set as the required system role(s) was/were not found.",
        content = [Content()],
      ),
    ],
  )
  fun retrieveMetricsSummary(
    @RequestParam(required = true)
    @Parameter(description = "The identifier of the given prison.", example = "MDI")
    prisonId: String,
    @RequestParam(required = true)
    @Parameter(description = "The start date of reporting period (in ISO-8601 date format)", example = "2024-01-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    dateFrom: LocalDate,
    @RequestParam(required = true)
    @Parameter(description = "The end date of reporting period (in ISO-8601 date format)", example = "2024-01-31")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    dateTo: LocalDate,
  ): ResponseEntity<GetMetricsSummaryResponse> {
    val response = applicationMetricsRetriever.retrieveMetricsSummaryByPrisonIdAndDates(prisonId, dateFrom, dateTo)
    return ResponseEntity.ok(response)
  }
}
