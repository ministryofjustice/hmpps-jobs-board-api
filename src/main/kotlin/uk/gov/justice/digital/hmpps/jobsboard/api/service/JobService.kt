package uk.gov.justice.digital.hmpps.jobsboard.api.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateJobRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider

@Service
class JobService(
  private val jobRepository: JobRepository,
  private val employerRepository: EmployerRepository,
  private val timeProvider: TimeProvider,
) {

  fun existsById(jobId: String): Boolean {
    return jobRepository.existsById(EntityId(jobId))
  }

  fun save(
    request: CreateJobRequest,
  ) {
    var employer = employerRepository.findById(EntityId(request.employerId!!)).orElseThrow { RuntimeException("Employer not found") }
    jobRepository.save(
      Job(
        request,
        employer,
      ),
    )
  }

  fun retrieve(id: String): Job? {
    return jobRepository.findById(EntityId(id)).orElseThrow { RuntimeException("Job not found") }
  }

  fun getPagingList(pageNo: Int, pageSize: Int, sortBy: String): MutableList<Job>? {
    val paging: Pageable = PageRequest.of(pageNo.toInt(), pageSize.toInt(), Sort.by(sortBy))

    val pagedResult: Page<Job> = jobRepository.findAll(paging)

    if (pagedResult.hasContent()) {
      return pagedResult.getContent()
    } else {
      return ArrayList<Job>()
    }
  }
}
