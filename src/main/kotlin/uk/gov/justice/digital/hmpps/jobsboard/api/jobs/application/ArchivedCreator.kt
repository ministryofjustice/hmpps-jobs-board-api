package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class ArchivedCreator(
  private val jobRepository: JobRepository,
  private val archivedRepository: ArchivedRepository,
) {

  @Transactional
  fun createOrUpdate(request: CreateArchivedRequest) {
    val job = jobRepository.findById(EntityId(request.jobId))
      .orElseThrow { IllegalArgumentException("Job not found: jobId=${request.jobId}") }

    val archived = Archived(
      id = JobPrisonerId(job.id, request.prisonNumber),
      job = job,
    )

    archivedRepository.save(archived)
  }

  fun existsById(jobId: String, prisonNumber: String): Boolean {
    if (jobRepository.findById(EntityId(jobId)).isEmpty) {
      throw IllegalArgumentException("Job not found: jobId=$jobId")
    }
    return archivedRepository.existsById(JobPrisonerId(EntityId(jobId), prisonNumber))
  }
}
