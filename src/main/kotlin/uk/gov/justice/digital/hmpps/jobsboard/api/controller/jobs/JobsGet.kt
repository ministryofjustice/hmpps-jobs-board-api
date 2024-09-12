package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

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
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.JobRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.MatchingCandidateJobRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

@Validated
@RestController
@RequestMapping("/jobs", produces = [APPLICATION_JSON_VALUE])
class JobsGet(
  private val jobRetriever: JobRetriever,
  private val matchingCandidateJobRetriever: MatchingCandidateJobRetriever,
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
    @RequestParam(required = false)
    sectors: List<String>?,
    @RequestParam(defaultValue = "0")
    page: Int,
    @RequestParam(defaultValue = "10")
    size: Int,
  ): ResponseEntity<Page<GetMatchingCandidateJobsResponse>> {
    val pageable: Pageable = PageRequest.of(page, size)
    val jobList = matchingCandidateJobRetriever.retrieveAllJobs(sectors, pageable)
    val response = jobList.map { GetMatchingCandidateJobsResponse.from(it) }
    return ResponseEntity.ok(response)
  }
}
