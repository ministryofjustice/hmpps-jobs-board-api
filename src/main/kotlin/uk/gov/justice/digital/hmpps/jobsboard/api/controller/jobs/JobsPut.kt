package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
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
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.CreateJobRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.JobCreator

@Validated
@RestController
@RequestMapping("/jobs", produces = [APPLICATION_JSON_VALUE])
class JobsPut(
  private val jobCreator: JobCreator,
) {
  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @PutMapping("/{id}")
  @Tags(Tag(name = "Popular"), Tag(name = "Jobs"))
  @Operation(
    summary = "Create or Update a Job",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "The success status is set as the creation request has been processed correctly.",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "200",
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
    id: String,
    @Valid @RequestBody createJobRequest: CreateJobRequest,
  ): ResponseEntity<Void> {
    val requestWithId = createJobRequest.copy(id = id)
    return if (jobCreator.existsById(id)) {
      jobCreator.update(requestWithId)
      ResponseEntity.ok().build()
    } else {
      jobCreator.create(requestWithId)
      ResponseEntity.created(
        ServletUriComponentsBuilder
          .fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(id)
          .toUri(),
      ).build()
    }
  }
}
