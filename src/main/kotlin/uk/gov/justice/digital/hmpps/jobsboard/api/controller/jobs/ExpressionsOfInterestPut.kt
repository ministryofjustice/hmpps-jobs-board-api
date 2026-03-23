package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.CreateExpressionOfInterestRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.ExpressionOfInterestCreator

@Validated
@RestController
@RequestMapping("/jobs/{jobId}/expressions-of-interest", produces = [APPLICATION_JSON_VALUE])
@Tag(name = "EOI")
class ExpressionsOfInterestPut(
  private val expressionOfInterestCreator: ExpressionOfInterestCreator,
) {
  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__JOBS__EOI__RW')")
  @PutMapping("/{prisonNumber}")
  @Operation(
    summary = "Register expression of interest for the prisoner",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "The success status is set as the creation request has been processed correctly.",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "200",
        description = "The success status is set as the it already exist and no creation is required.",
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
  fun create(
    @PathVariable
    @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format",
    )
    jobId: String,
    @PathVariable
    @Size(max = 7, min = 1)
    prisonNumber: String,
  ): ResponseEntity<Void> = if (expressionOfInterestCreator.existsById(jobId, prisonNumber)) {
    ResponseEntity.ok().build()
  } else {
    expressionOfInterestCreator.createOrUpdate(
      CreateExpressionOfInterestRequest(jobId, prisonNumber),
    )
    ResponseEntity.created(
      ServletUriComponentsBuilder
        .fromCurrentRequest()
        .build()
        .toUri(),
    ).build()
  }

  @Hidden
  @PutMapping("/")
  fun notFound(): ResponseEntity<Void> = ResponseEntity.notFound().build()
}
