package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import java.time.LocalDate

@Service
class MatchingCandidateJobRetriever(
  private val matchingCandidateJobsRepository: MatchingCandidateJobRepository,
  private val postcodeLocationService: PostcodeLocationService,
) {

  fun retrieveAllJobs(prisonNumber: String, sectors: List<String>?, location: String?, distance: Float?, pageable: Pageable): Page<GetMatchingCandidateJobsResponse> {
    location?.let { postcodeLocationService.save(it) }
    return matchingCandidateJobsRepository.findAll(prisonNumber, sectors, location, pageable)
  }

  fun retrieveClosingJobs(prisonNumber: String, sectors: List<String>?, size: Int): List<GetJobsClosingSoonResponse> {
    return PageRequest.of(0, size).let { limitedBySize ->
      matchingCandidateJobsRepository.findJobsClosingSoon(
        prisonNumber = prisonNumber,
        sectors = sectors?.map { it.lowercase() },
        currentDate = LocalDate.now(),
        pageable = limitedBySize,
      )
    }
  }
}
