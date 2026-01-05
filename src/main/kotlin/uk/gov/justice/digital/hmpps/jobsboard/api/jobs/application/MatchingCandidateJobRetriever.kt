package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.jpa.domain.JpaSort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.CALC_DISTANCE_EXPRESSION
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider
import java.time.LocalDate

@Service
class MatchingCandidateJobRetriever(
  private val matchingCandidateJobsRepository: MatchingCandidateJobRepository,
  private val postcodeLocationService: PostcodeLocationService,
  private val timeProvider: TimeProvider,
) {
  private val today: LocalDate get() = timeProvider.today()

  fun retrieveAllJobs(
    prisonNumber: String,
    sectors: List<String>?,
    releaseArea: String?,
    searchRadius: Int?,
    pageable: Pageable,
    isNationalJob: Boolean? = false,
    employerId: String?,
  ): Page<GetMatchingCandidateJobsResponse> {
    releaseArea?.let { postcodeLocationService.save(it) }
    var maybeRevisedSearchRadiusInput = searchRadius
    var maybeRevisedReleaseAreaInput = releaseArea

    if (!postcodeLocationService.isGeoCoded(releaseArea) || isNationalJob == true) {
      maybeRevisedSearchRadiusInput = null
      maybeRevisedReleaseAreaInput = null
    }

    return matchingCandidateJobsRepository.findAll(prisonNumber, sectors, maybeRevisedReleaseAreaInput, maybeRevisedSearchRadiusInput, today, isNationalJob, employerId, pageable)
  }

  fun retrieveClosingJobs(prisonNumber: String, sectors: List<String>?, size: Int): List<GetJobsClosingSoonResponse> = PageRequest.of(0, size).let { limitedBySize ->
    matchingCandidateJobsRepository.findJobsClosingSoon(
      prisonNumber = prisonNumber,
      sectors = sectors?.map { it.lowercase() },
      currentDate = today,
      pageable = limitedBySize,
    )
  }

  fun retrieveClosingJobsOfInterest(prisonNumber: String): List<GetJobsClosingSoonResponse> = matchingCandidateJobsRepository.findJobsOfInterestClosingSoon(prisonNumber, today)

  fun retrieveJobsOfInterest(
    prisonNumber: String,
    releaseAreaPostcode: String?,
    pageable: Pageable,
  ): Page<GetMatchingCandidateJobsResponse> {
    releaseAreaPostcode?.let { postcodeLocationService.save(it) }
    return matchingCandidateJobsRepository.findJobsOfInterest(
      prisonNumber = prisonNumber,
      releaseAreaPostcode = releaseAreaPostcode,
      currentDate = today,
      pageable = pageable,
    )
  }

  fun retrieveArchivedJobs(
    prisonNumber: String,
    releaseAreaPostcode: String?,
    pageable: Pageable,
  ): Page<GetMatchingCandidateJobsResponse> {
    releaseAreaPostcode?.let { postcodeLocationService.save(it) }
    return matchingCandidateJobsRepository.findArchivedJobs(
      prisonNumber = prisonNumber,
      releaseAreaPostcode = releaseAreaPostcode,
      currentDate = today,
      pageable = pageable,
    )
  }

  fun sortByDistance(direction: Direction = Direction.ASC): Sort = JpaSort.unsafe(direction, CALC_DISTANCE_EXPRESSION)
}
