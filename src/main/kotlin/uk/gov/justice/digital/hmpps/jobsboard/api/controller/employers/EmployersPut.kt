package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
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
import uk.gov.justice.digital.hmpps.jobsboard.api.config.DataErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.application.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.application.EmployerCreator

@Validated
@RestController
@RequestMapping("/employers", produces = [APPLICATION_JSON_VALUE])
class EmployersPut(
  private val employerCreator: EmployerCreator,
) {
  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__EMPLOYERS__RW')")
  @PutMapping("/{id}")
  @Operation(
    summary = "Create or update an Employer ",
    description = "Create or update a Jobs Board Employer. Currently requires role <b>ROLE_EDUCATION_WORK_PLAN_EDIT</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Employer updated",
      ),
      ApiResponse(
        responseCode = "201",
        description = "Employer created",
      ),
      ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = DataErrorResponse::class),
            examples = [
              ExampleObject(
                value = """
                {
                  "status": 400,
                  "userMessage": "Validation failed",
                  "error": "Bad Request",
                  "details": [
                    {
                      "field": "name",
                      "message": "The name provided already exists. Please choose a different name.",
                      "code": "DUPLICATE_EMPLOYER"
                    }
                  ],
                  "timestamp": "2025-01-30T12:34:56.789Z"
                }
                """,
              ),
            ],
          ),
        ],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Error: Unauthorised. The error status is set as the required authorisation was not provided.",
      ),
      ApiResponse(
        responseCode = "403",
        description = "Error: Access Denied. The error status is set as the required system role(s) was/were not found.",
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
    val request = createEmployerRequest.copy(id = id).also {
      employerCreator.validate(it)
    }

    return if (employerCreator.existsById(id)) {
      employerCreator.update(request)
      ResponseEntity.ok().build()
    } else {
      employerCreator.create(request)
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
