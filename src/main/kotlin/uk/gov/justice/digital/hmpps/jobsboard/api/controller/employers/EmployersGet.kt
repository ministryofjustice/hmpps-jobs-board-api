package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import io.swagger.v3.oas.annotations.Operation
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.application.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.application.EmployerRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.application.GetEmployerResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer

@Validated
@RestController
@RequestMapping("/employers", produces = [APPLICATION_JSON_VALUE])
class EmployersGet(
  private val employerRetriever: EmployerRetriever,
) {
  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__EMPLOYERS__RO','ROLE_JOBS_BOARD__EMPLOYERS__RW')")
  @GetMapping("/{id}")
  @Operation(
    summary = "Retrieve an Employer ",
    description = "Retrieve a Jobs Board Employer. Currently requires roles <b>ROLE_EDUCATION_WORK_PLAN_VIEW</b>, <b>ROLE_EDUCATION_WORK_PLAN_EDIT</b> or <b>ROLE_JOBS_BOARD__EMPLOYERS__RO</b>",
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
  fun retrieve(
    @PathVariable id: String,
  ): ResponseEntity<GetEmployerResponse> {
    val employer: Employer = employerRetriever.retrieve(id)
    return ResponseEntity.ok().body(GetEmployerResponse.from(employer))
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__EMPLOYERS__RO','ROLE_JOBS_BOARD__EMPLOYERS__RW')")
  @GetMapping("")
  @Operation(
    summary = "Retrieve all employers",
    description = "Retrieve all employers. Currently requires role <b>ROLE_EDUCATION_WORK_PLAN_VIEW</b>, <b>ROLE_EDUCATION_WORK_PLAN_EDIT</b> or <b>ROLE_JOBS_BOARD__EMPLOYERS__RO</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "The success status is set as the request has been processed correctly.",
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
  fun retrieveAll(
    @RequestParam(required = false)
    name: String?,
    @RequestParam(required = false)
    sector: String?,
    @RequestParam(defaultValue = "name", required = false)
    sortBy: String?,
    @RequestParam(defaultValue = "asc", required = false)
    sortOrder: String?,
    @RequestParam(defaultValue = "0")
    page: Int,
    @RequestParam(defaultValue = "10")
    size: Int,
    @RequestParam(defaultValue = "false")
    hasNationalJobs: Boolean,
  ): ResponseEntity<Page<GetEmployerResponse>>? {
    val direction = if (sortOrder.equals("desc", ignoreCase = true)) DESC else ASC
    val pageable: Pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
    val employerList = employerRetriever.retrieveAllEmployers(name, sector, pageable, hasNationalJobs)
    val response = employerList.map { GetEmployerResponse.from(it) }
    return ResponseEntity.ok(response)
  }
}
