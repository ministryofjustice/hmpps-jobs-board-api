package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository

@Service
class MatchingCandidateJobRetriever(
  private val matchingCandidateJobsRepository: MatchingCandidateJobRepository,
) {

  fun retrieveAllJobs(sectors: List<String>?, pageable: Pageable): Page<Job> {
    return when {
      !sectors.isNullOrEmpty() -> matchingCandidateJobsRepository.findBySectorInIgnoringCase(
        sectors = sectors,
        pageable = pageable,
      )
      else -> matchingCandidateJobsRepository.findAll(pageable)
    }
  }
}
