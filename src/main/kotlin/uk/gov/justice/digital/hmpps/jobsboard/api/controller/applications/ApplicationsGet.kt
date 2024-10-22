package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationByPrisonerRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.GetApplicationsByPrisonerResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse

@Validated
@RestController
@RequestMapping("/applications", produces = [APPLICATION_JSON_VALUE])
class ApplicationsGet(
  private val applicationsRetriever: ApplicationByPrisonerRetriever,
) {
  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/open")
  @Operation(
    summary = "Retrieve open applications of the given prisoner",
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
  fun retrieveOpenApplications(
    @RequestParam(required = true)
    @Parameter(description = "The identifier (prison number) of the given prisoner")
    prisonNumber: String,
    @RequestParam(defaultValue = "0")
    page: Int,
    @RequestParam(defaultValue = "10")
    size: Int,
  ): ResponseEntity<Page<GetApplicationsByPrisonerResponse>> {
    val pageable: Pageable = PageRequest.of(page, size, Sort.by(ASC, "createdAt"))
    val openApplications = applicationsRetriever.retrieveAllOpenApplications(prisonNumber, pageable)
    val response = openApplications.map { GetApplicationsByPrisonerResponse.from(it) }
    return ResponseEntity.ok(response)
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/closed")
  @Operation(
    summary = "Retrieve closed applications of the given prisoner",
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
  fun retrieveClosedApplications(
    @RequestParam(required = true)
    @Parameter(description = "The identifier (prison number) of the given prisoner")
    prisonNumber: String,
    @RequestParam(defaultValue = "0")
    page: Int,
    @RequestParam(defaultValue = "10")
    size: Int,
  ): ResponseEntity<Page<GetApplicationsByPrisonerResponse>> {
    val pageable: Pageable = PageRequest.of(page, size, Sort.by(DESC, "lastModifiedAt"))
    val openApplications = applicationsRetriever.retrieveAllClosedApplications(prisonNumber, pageable)
    val response = openApplications.map { GetApplicationsByPrisonerResponse.from(it) }
    return ResponseEntity.ok(response)
  }
}
