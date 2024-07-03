package uk.gov.justice.digital.hmpps.jobsboard.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.GetEmployerResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.JobEmployerDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.service.EmployerService

@Validated
@RestController
@RequestMapping("/employers", produces = [MediaType.APPLICATION_JSON_VALUE])
class EmployerController(
  private val jobEmployerService: EmployerService,
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
  fun createEmployer(
    @PathVariable id: String,
    @Valid @RequestBody createEmployerRequest: CreateEmployerRequest,
  ): ResponseEntity<String> {
    jobEmployerService.createEmployer(createEmployerRequest.copy(id = id))
    return ResponseEntity.created(
      ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(id)
        .toUri(),
    ).build()
  }

  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_VIEW') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/{id}")
  @Operation(
    summary = "Retrieve an Employer ",
    description = "Retrieve a Jobs Board Employer. Currently requires roles <b>ROLE_EDUCATION_WORK_PLAN_VIEW</b> or <b>ROLE_EDUCATION_WORK_PLAN_EDIT</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Employer exists",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = CreateEmployerRequest::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Employer not found",
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
  fun retrieveEmployer(
    @PathVariable id: String,
  ): ResponseEntity<GetEmployerResponse> {
    val employer: Employer = jobEmployerService.retrieveEmployer(id)
    return ResponseEntity.ok().body(GetEmployerResponse.from(employer))
  }

  @PreAuthorize("hasRole('WORK_READINESS_EDIT') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/test/list")
  @Operation(
    summary = "Create a job employer ",
    description = "Create a job employer. Currently requires role <b>ROLE_VIEW_PRISONER_DATA</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Job Employer  created",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = JobEmployerDTO::class),
          ),
        ],
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
  fun getEmployerlist(
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    pageNo: Int,
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    pageSize: Int,
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    sortBy: String,
  ): MutableList<Employer>? {
    return jobEmployerService.getPagingList(pageNo, pageSize, sortBy)
  }
}
