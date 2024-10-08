package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
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
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobResponse
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
    val jobList = matchingCandidateJobRetriever.retrieveAllJobs(prisonNumber, lowerCaseSectors, pageable)
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
    postcode: String?,
  ): ResponseEntity<GetMatchingCandidateJobResponse> {
    val details = matchingCandidateJobDetailsRetriever.retrieve(id, prisonNumber)
    return when {
      details != null -> ResponseEntity.ok(details)
      else -> ResponseEntity.notFound().build()
    }
  }
}
