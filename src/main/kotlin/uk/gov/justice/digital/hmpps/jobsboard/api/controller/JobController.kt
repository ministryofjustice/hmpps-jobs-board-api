package uk.gov.justice.digital.hmpps.jobsboard.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateJobRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.GetJobResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.service.JobService

@Validated
@RestController
@RequestMapping("/candidate-matching/job", produces = [MediaType.APPLICATION_JSON_VALUE])
class JobController(
  private val jobService: JobService,
) {
  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @PutMapping("/{id}")
  @Operation(
    summary = "Create a job ",
    description = "Create a a Job in Jobs Board. Currently requires role <b>ROLE_EDUCATION_WORK_PLAN_EDIT</b>",
    responses = [
      ApiResponse(
        responseCode = "201",
        description = "Job created",
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
  fun save(
    @PathVariable
    @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format",
    )
    id: String,
    @Valid @RequestBody createJobRequest: CreateJobRequest,
  ): ResponseEntity<Void> {
    val jobExists = jobService.existsById(id)
    jobService.save(createJobRequest.copy(id = id))

    return if (jobExists) {
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

  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_VIEW') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/{id}")
  @Operation(
    summary = "Retrieve an Job ",
    description = "Retrieve a Jobs Board Employer. Currently requires roles <b>ROLE_EDUCATION_WORK_PLAN_VIEW</b> or <b>ROLE_EDUCATION_WORK_PLAN_EDIT</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Job exists",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CreateEmployerRequest::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Job not found",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
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
  fun retrieve(
    @PathVariable id: String,
  ): ResponseEntity<GetJobResponse> {
    val job = jobService.retrieve(id)
    return ResponseEntity.ok().body(GetJobResponse.from(job!!))
  }
}
