package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import kotlin.jvm.optionals.getOrNull

@Service
class MatchingCandidateJobDetailsRetriever(
  private val matchingCandidateJobsRepository: MatchingCandidateJobRepository,
  private val postcodeLocationService: PostcodeLocationService,
  private val jobRepository: JobRepository,
) {
  fun retrieve(jobId: String, prisonNumber: String? = null, releaseAreaPostcode: String? = null): GetMatchingCandidateJobResponse? {
    releaseAreaPostcode?.let { postcodeLocationService.save(it) }

    return when {
      prisonNumber.isNullOrEmpty() -> jobRepository.findById(EntityId(jobId)).getOrNull()
        ?.let { job -> GetMatchingCandidateJobResponse.from(job) }

      else -> matchingCandidateJobsRepository.findJobDetailsByPrisonNumberAndReleaseAreaPostcode(jobId, prisonNumber, releaseAreaPostcode).firstOrNull()
    }
  }
}
