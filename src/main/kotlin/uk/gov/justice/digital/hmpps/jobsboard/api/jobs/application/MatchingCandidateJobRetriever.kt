package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository

@Service
class MatchingCandidateJobRetriever(
  private val matchingCandidateJobsRepository: MatchingCandidateJobRepository,
) {

  fun retrieveAllJobs(prisonNumber: String, sectors: List<String>?, pageable: Pageable): Page<GetMatchingCandidateJobsResponse> {
    return matchingCandidateJobsRepository.findAll(prisonNumber, sectors, pageable)
  }
}
