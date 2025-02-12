package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class ArchivedDeleter(
  private val jobRepository: JobRepository,
  private val archivedRepository: ArchivedRepository,
) {

  @Transactional
  fun delete(request: DeleteArchivedRequest) = archivedRepository.deleteById(
    JobPrisonerId(
      jobId = EntityId(request.jobId),
      prisonNumber = request.prisonNumber,
    ),
  )

  fun existsById(jobId: String, prisonNumber: String): Boolean {
    if (jobRepository.findById(EntityId(jobId)).isEmpty) {
      throw IllegalArgumentException("Job not found: jobId=$jobId")
    }
    return archivedRepository.existsById(JobPrisonerId(EntityId(jobId), prisonNumber))
  }
}
