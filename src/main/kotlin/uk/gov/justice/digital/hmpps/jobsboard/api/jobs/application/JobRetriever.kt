package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class JobRetriever(
  private val jobRepository: JobRepository,
) {

  fun retrieve(id: String): Job {
    return jobRepository.findById(EntityId(id)).orElseThrow()
  }

  fun retrieveAllJobs(jobTitleOrEmployerName: String?, sector: String?, pageable: Pageable): Page<Job> {
    return when {
      !jobTitleOrEmployerName.isNullOrEmpty() && sector.isNullOrEmpty() ->
        jobRepository
          .findByTitleContainingOrEmployerNameContainingAllIgnoringCase(
            title = jobTitleOrEmployerName,
            employerName = jobTitleOrEmployerName,
            pageable = pageable,
          )
      jobTitleOrEmployerName.isNullOrEmpty() && !sector.isNullOrEmpty() ->
        jobRepository.findBySectorIgnoringCase(
          sector = sector,
          pageable = pageable,
        )
      !jobTitleOrEmployerName.isNullOrEmpty() && !sector.isNullOrEmpty() ->
        jobRepository.findBySectorAndTitleOrEmployerName(
          searchString = jobTitleOrEmployerName,
          sector = sector,
          pageable = pageable,
        )
      else -> jobRepository.findAll(pageable)
    }
  }
}
