package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobsClosingSoonResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobsResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.JobRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.MatchingCandidateJobDetailsRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.MatchingCandidateJobRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

@Validated
@RestController
@RequestMapping("/jobs", produces = [APPLICATION_JSON_VALUE])
class JobsGet(
  private val jobRetriever: JobRetriever,
  private val matchingCandidateJobRetriever: MatchingCandidateJobRetriever,
  private val matchingCandidateJobDetailsRetriever: MatchingCandidateJobDetailsRetriever,
) {

  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_VIEW') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/{id}")
  fun retrieve(@PathVariable id: String): ResponseEntity<GetJobResponse> {
    val job: Job = jobRetriever.retrieve(id)
    return ResponseEntity.ok().body(GetJobResponse.from(job))
  }

  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_VIEW') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("")
  fun retrieveAll(
    @RequestParam(required = false)
    jobTitleOrEmployerName: String?,
    @RequestParam(required = false)
    sector: String?,
    @RequestParam(defaultValue = "title", required = false)
    sortBy: String?,
    @RequestParam(defaultValue = "asc", required = false)
    sortOrder: String?,
    @RequestParam(defaultValue = "0")
    page: Int,
    @RequestParam(defaultValue = "10")
    size: Int,
  ): ResponseEntity<Page<GetJobsResponse>> {
    val sortedBy = when (sortBy) {
      "jobTitle" -> "title"
      else -> sortBy
    }
    val direction = if (sortOrder.equals("desc", ignoreCase = true)) DESC else ASC
    val pageable: Pageable = PageRequest.of(page, size, Sort.by(direction, sortedBy))
    val jobList = jobRetriever.retrieveAllJobs(jobTitleOrEmployerName, sector, pageable)
    val response = jobList.map { GetJobsResponse.from(it) }
    return ResponseEntity.ok(response)
  }

  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_VIEW') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/matching-candidate")
  fun retrieveAll(
    @RequestParam(required = true)
    prisonNumber: String,
    @RequestParam(required = false)
    sectors: List<String>? = null,
    @RequestParam(required = false)
    releaseArea: String? = null,
    @RequestParam(required = false)
    searchRadius: Float? = null,
    @RequestParam(defaultValue = "title", required = false)
    sortBy: String?,
    @RequestParam(defaultValue = "asc", required = false)
    sortOrder: String?,
    @RequestParam(defaultValue = "0")
    page: Int,
    @RequestParam(defaultValue = "10")
    size: Int,
  ): ResponseEntity<Page<GetMatchingCandidateJobsResponse>> {
    val sortedBy = when (sortBy) {
      "jobTitle" -> "title"
      else -> sortBy
    }
    val lowerCaseSectors = sectors?.map { it.lowercase() }
    val direction = if (sortOrder.equals("desc", ignoreCase = true)) DESC else ASC
    val pageable: Pageable = PageRequest.of(page, size, Sort.by(direction, sortedBy))
    val jobList = matchingCandidateJobRetriever.retrieveAllJobs(prisonNumber, lowerCaseSectors, releaseArea, searchRadius, pageable)
    return ResponseEntity.ok(jobList)
  }

  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_VIEW') or  hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/{id}/matching-candidate")
  @Operation(
    summary = "Retrieve Job details, while matching candidate",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "The success status is set as the request has been processed correctly.",
      ),
      ApiResponse(
        responseCode = "404",
        description = "The failure status is set when the job is not found (Job has been deleted or a wrong job ID was provided).",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Incorrect permissions to access this endpoint",
        useReturnTypeSchema = true,
        content = [Content()],
      ),
    ],
  )
  fun retrieve(
    @PathVariable
    @Parameter(description = "Job ID, identifier of the given job")
    id: String,
    @RequestParam(required = false)
    @Parameter(description = "The identifier (prison number) of the given prisoner")
    prisonNumber: String?,
    @RequestParam(required = false)
    @Parameter(description = "The release area’s postcode of the given prisoner")
    releaseArea: String?,
  ): ResponseEntity<GetMatchingCandidateJobResponse> {
    val details = matchingCandidateJobDetailsRetriever.retrieve(id, prisonNumber, releaseArea)
    return when {
      details != null -> ResponseEntity.ok(details)
      else -> ResponseEntity.notFound().build()
    }
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW', 'ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/matching-candidate/closing-soon")
  @Operation(
    summary = "Retrieve jobs closing soon, matching the given prisoner",
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
  fun retrieveClosingJobs(
    @RequestParam(required = true)
    @Parameter(description = "The identifier (prison number) of the given prisoner")
    prisonNumber: String,
    @RequestParam(required = false)
    @Parameter(description = "A list of provided types of work. Known as the job sector.")
    sectors: List<String>? = null,
    @RequestParam(defaultValue = "3")
    @Parameter(description = "Restricted size of the results; default size is 3. Results will be restricted to only top-size jobs.")
    size: Int,
  ): ResponseEntity<List<GetJobsClosingSoonResponse>> {
    return matchingCandidateJobRetriever.retrieveClosingJobs(prisonNumber, sectors, size).let {
      ResponseEntity.ok(it)
    }
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW', 'ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/expressed-interest/closing-soon")
  @Operation(
    summary = "Retrieve jobs of interest closing soon, for the given prisoner",
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
  fun retrieveClosingJobsOfInterest(
    @RequestParam(required = true)
    @Parameter(description = "The identifier (prison number) of the given prisoner")
    prisonNumber: String,
  ): ResponseEntity<List<GetJobsClosingSoonResponse>> {
    return matchingCandidateJobRetriever.retrieveClosingJobsOfInterest(prisonNumber).let {
      ResponseEntity.ok(it)
    }
  }

  @PreAuthorize("hasAnyRole('ROLE_EDUCATION_WORK_PLAN_VIEW', 'ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/expressed-interest")
  @Operation(
    summary = "Retrieve jobs of interest, for the given prisoner",
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
  fun retrieveJobsOfInterest(
    @RequestParam(required = true)
    @Parameter(description = "The identifier (prison number) of the given prisoner")
    prisonNumber: String,
    @RequestParam(required = false)
    @Parameter(description = "The release area's postcode of the given prisoner")
    releaseArea: String? = null,
    @RequestParam(defaultValue = "closingDate")
    @Parameter(description = "Defines the attribute used to sort the Job list")
    sortBy: String?,
    @RequestParam(defaultValue = "asc")
    @Parameter(description = "Defines the sorting order")
    sortOrder: String?,
    @RequestParam(defaultValue = "0")
    @Parameter(description = "Which page to be returned. 0-based index (Page 0 is first page)")
    page: Int,
    @RequestParam(defaultValue = "20")
    @Parameter(description = "Number of items expected per page")
    size: Int,
  ): ResponseEntity<Page<GetMatchingCandidateJobsResponse>> {
    val direction = if (sortOrder.equals("desc", ignoreCase = true)) DESC else ASC
    val sort = when (sortBy?.lowercase()) {
      "jobAndEmployer".lowercase() -> Sort.by(direction, "title", "employer.name")
      "location" -> matchingCandidateJobRetriever.sortByDistance(direction)
      else -> Sort.by(direction, "closingDate")
    }
    val pageable: Pageable = PageRequest.of(page, size, sort)

    return matchingCandidateJobRetriever.retrieveJobsOfInterest(prisonNumber, releaseArea, pageable).let {
      ResponseEntity.ok(it)
    }
  }
}
