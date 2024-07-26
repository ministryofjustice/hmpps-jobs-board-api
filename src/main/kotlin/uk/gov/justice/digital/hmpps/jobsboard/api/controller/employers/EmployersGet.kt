package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
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
@RequestMapping("/employers", produces = [MediaType.APPLICATION_JSON_VALUE])
class EmployersGet(
  private val employerRetriever: EmployerRetriever,
) {
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
  fun retrieve(
    @PathVariable id: String,
  ): ResponseEntity<GetEmployerResponse> {
    val employer: Employer = employerRetriever.retrieve(id)
    return ResponseEntity.ok().body(GetEmployerResponse.from(employer))
  }

  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_VIEW') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("")
  @Operation(
    summary = "Create a job employer ",
    description = "Create a job employer. Currently requires role <b>ROLE_VIEW_PRISONER_DATA</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Job Employer  created",
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
  ): ResponseEntity<Page<GetEmployerResponse>>? {
    val direction = if (sortOrder.equals("desc", ignoreCase = true)) Sort.Direction.DESC else Sort.Direction.ASC
    val pageable: Pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
    val employerList = employerRetriever.retrieveAllEmployers(name, sector, pageable)
    val response = employerList.map { GetEmployerResponse.from(it) }
    return ResponseEntity.ok(response)
  }
}
