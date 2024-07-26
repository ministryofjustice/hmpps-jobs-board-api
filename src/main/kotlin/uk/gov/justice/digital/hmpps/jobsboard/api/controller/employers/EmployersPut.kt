package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import io.swagger.v3.oas.annotations.Operation
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
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.application.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.application.EmployerCreator

@Validated
@RestController
@RequestMapping("/employers", produces = [APPLICATION_JSON_VALUE])
class EmployersPut(
  private val employerCreator: EmployerCreator,
) {
  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @PutMapping("/{id}")
  @Operation(
    summary = "Create an Employer ",
    description = "Create a Jobs Board Employer. Currently requires role <b>ROLE_EDUCATION_WORK_PLAN_EDIT</b>",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Employer created",
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Incorrect permissions to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
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
    @Valid @RequestBody createEmployerRequest: CreateEmployerRequest,
  ): ResponseEntity<Void> {
    val employerExists = employerCreator.existsById(id)
    employerCreator.createOrUpdate(createEmployerRequest.copy(id = id))

    return if (employerExists) {
      ResponseEntity.ok().build()
    } else {
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
