package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.JobRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

@Validated
@RestController
@RequestMapping("/jobs", produces = [APPLICATION_JSON_VALUE])
class JobsGet(private val jobRetriever: JobRetriever) {

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
    @RequestParam(defaultValue = "0")
    page: Int,
    @RequestParam(defaultValue = "10")
    size: Int,
  ): ResponseEntity<Page<GetJobResponse>> {
    val pageable: Pageable = PageRequest.of(page, size)
    val jobList = jobRetriever.retrieveAllJobs(jobTitleOrEmployerName, pageable)
    val response = jobList.map { GetJobResponse.from(it) }
    return ResponseEntity.ok(response)
  }
}
