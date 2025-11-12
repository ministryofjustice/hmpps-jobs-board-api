package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationCreator
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.CreateApplicationRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse

@Validated
@RestController
@RequestMapping("/applications", produces = [APPLICATION_JSON_VALUE])
class ApplicationsPut(
  private val applicationCreator: ApplicationCreator,
) {
  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__APPLICATIONS__RW')")
  @PutMapping("/{id}")
  @Operation(
    summary = "Create or Update an Application to a job for the prisoner",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "The success status is set as the creation request has been processed correctly.",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "201",
        description = "The success status is set as the update request has been processed correctly.",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "400",
        description = "The failure status is set when the request is invalid. An error response will be provided.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Incorrect permissions to access this endpoint",
        content = [Content()],
      ),
    ],
  )
  fun createOrUpdate(
    @PathVariable
    @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format",
    )
    @Parameter(description = "Application ID, identifier of the application to the given job")
    id: String,
    @Valid @RequestBody createApplicationRequest: CreateApplicationRequest,
  ): ResponseEntity<Void> {
    val exists = applicationCreator.existsById(id)
    applicationCreator.createOrUpdate(createApplicationRequest.copy(id = id))
    return if (exists) {
      ResponseEntity.ok().build()
    } else {
      ResponseEntity.created(
        ServletUriComponentsBuilder
          .fromCurrentRequest()
          .build()
          .toUri(),
      ).build()
    }
  }
}
