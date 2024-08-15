package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class JobRetriever(
  private val jobRepository: JobRepository,
) {

  fun retrieve(id: String): Job {
    return jobRepository.findById(EntityId(id)).orElseThrow()
  }

  fun retrieveAllJobs(pageable: Pageable): Page<Job> {
    return jobRepository.findAll(pageable)
  }
}
