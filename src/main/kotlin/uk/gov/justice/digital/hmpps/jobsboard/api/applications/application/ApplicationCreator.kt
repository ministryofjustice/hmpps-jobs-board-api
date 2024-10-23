package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository

@Service
class ApplicationCreator(
  private val applicationRepository: ApplicationRepository,
  private val jobRepository: JobRepository,
  private val matchingCandidateJobsRepository: MatchingCandidateJobRepository,
) {
  fun createOrUpdate(request: CreateApplicationRequest) {
    JobPrisonerId.validatePrisonNumber(request.prisonNumber)

    val job = jobRepository.findById(EntityId(request.jobId))
      .orElseThrow { IllegalArgumentException("Job not found: jobId=${request.jobId}") }

    matchingCandidateJobsRepository.findJobDetailsByPrisonNumber(request.jobId, request.prisonNumber).firstOrNull()
      ?.let { jobDetails ->
        if (jobDetails.archived) {
          throw IllegalArgumentException("Job has been archived for the prisoner: jobId=${request.jobId}, prisonNumber=${request.prisonNumber}")
        }
      }

    save(request, job)
  }

  fun existsById(id: String) = applicationRepository.existsById(EntityId(id))

  private fun save(request: CreateApplicationRequest, job: Job) {
    applicationRepository.save(
      Application(
        id = EntityId(request.id),
        prisonNumber = request.prisonNumber,
        prisonId = request.prisonId,
        firstName = request.firstName,
        lastName = request.lastName,
        status = request.applicationStatus,
        additionalInformation = request.additionalInformation,
        job = job,
      ),
    )
  }
}
