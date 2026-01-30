package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
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
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationHistoryRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.GetApplicationHistoriesResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.GetApplicationsByPrisonerResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.GetApplicationsResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse

@Validated
@RestController
@RequestMapping("/applications", produces = [APPLICATION_JSON_VALUE])
class ApplicationsGet(
  private val applicationRetriever: ApplicationRetriever,
  private val applicationByPrisonerRetriever: ApplicationByPrisonerRetriever,
  private val applicationHistoryRetriever: ApplicationHistoryRetriever,
) {
  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__APPLICATIONS__RW')")
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
    val openApplications = applicationByPrisonerRetriever.retrieveAllOpenApplications(prisonNumber, pageable)
    val response = openApplications.map { GetApplicationsByPrisonerResponse.from(it) }
    return ResponseEntity.ok(response)
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__APPLICATIONS__RW')")
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
    val closedApplications = applicationByPrisonerRetriever.retrieveAllClosedApplications(prisonNumber, pageable)
    val response = closedApplications.map { GetApplicationsByPrisonerResponse.from(it) }
    return ResponseEntity.ok(response)
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__APPLICATIONS__RW')")
  @GetMapping("/histories")
  @Operation(
    summary = "Retrieve histories of an application for the given prisoner",
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
  fun retrieveApplicationHistories(
    @RequestParam(required = true)
    @Parameter(description = "The identifier (prison number) of the given prisoner")
    prisonNumber: String,
    @RequestParam(required = true)
    @Parameter(description = "The identifier of the job")
    jobId: String,
  ): ResponseEntity<List<GetApplicationHistoriesResponse>> {
    val revisions = applicationHistoryRetriever.retrieveAllApplicationHistories(prisonNumber, jobId)
    val response =
      revisions?.map { GetApplicationHistoriesResponse.from(it) }?.toList() ?: listOf()
    return ResponseEntity.ok(response)
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW','ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__APPLICATIONS__RW')")
  @GetMapping("")
  @Operation(
    summary = "Retrieve applications of the given prison",
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
  fun retrieveApplicationsByPrisonId(
    @RequestParam(required = true)
    @Parameter(description = "The identifier of the given prison.", example = "MDI")
    prisonId: String,
    @RequestParam(required = false)
    @Parameter(description = "Prisoner's Name to be searched with")
    prisonerName: String?,
    @RequestParam(required = false)
    @Parameter(description = "Text searching against Job Title or Employer Name")
    jobTitleOrEmployerName: String?,
    @RequestParam(required = false)
    @Parameter(
      description = "Application Status",
      example = "APPLICATION_MADE",
      examples = [
        ExampleObject(name = "APPLICATION_MADE", description = "Application has been made"),
        ExampleObject(name = "APPLICATION_UNSUCCESSFUL", description = "Application is unsuccessful"),
        ExampleObject(name = "SELECTED_FOR_INTERVIEW", description = "Selected for interview"),
        ExampleObject(name = "INTERVIEW_BOOKED", description = "Interview has been booked"),
        ExampleObject(name = "UNSUCCESSFUL_AT_INTERVIEW", description = "Unsuccessful at interview"),
        ExampleObject(name = "JOB_OFFER", description = "Job offer"),
      ],
    )
    applicationStatus: List<String>?,
    @RequestParam(defaultValue = "prisonerName", required = false)
    @Parameter(
      description = "Sorting by (prisoner name / job title and employer name)",
      example = "jobAndEmployer",
      examples = [
        ExampleObject(
          name = "prisonerName",
          value = "prisonerName",
          description = "Sort by prisoner's last name, first name",
        ),
        ExampleObject(
          name = "jobAndEmployer",
          value = "jobAndEmployer",
          description = "Sort by Job Title, Employer Name",
        ),
      ],
    )
    sortBy: String?,
    @RequestParam(defaultValue = "asc", required = false)
    @Parameter(
      description = "Sorting order (ascending/descending)",
      example = "desc",
      examples = [
        ExampleObject(name = "asc", value = "asc", description = "ascending order"),
        ExampleObject(name = "desc", value = "desc", description = "descending order"),
      ],
    )
    sortOrder: String?,
    @RequestParam(defaultValue = "0")
    @Parameter(description = "Page number")
    page: Int,
    @RequestParam(defaultValue = "20")
    @Parameter(description = "Page size")
    size: Int,
  ): ResponseEntity<Page<GetApplicationsResponse>> {
    val direction = if (sortOrder.equals("desc", ignoreCase = true)) DESC else ASC
    val sortByFields = when {
      sortBy?.equals("jobAndEmployer", ignoreCase = true) ?: false -> arrayOf("job.title", "job.employer.name")
      else -> arrayOf("lastName", "firstName")
    }
    val pageable: Pageable = PageRequest.of(page, size, Sort.by(direction, *sortByFields))
    val applications = applicationRetriever.retrieveAllApplicationsByPrisonId(
      prisonId,
      prisonerName = prisonerName,
      status = applicationStatus,
      jobTitleOrEmployerName = jobTitleOrEmployerName,
      pageable = pageable,
    )
    val response = applications.map { GetApplicationsResponse.from(it) }
    return ResponseEntity.ok(response)
  }
}
