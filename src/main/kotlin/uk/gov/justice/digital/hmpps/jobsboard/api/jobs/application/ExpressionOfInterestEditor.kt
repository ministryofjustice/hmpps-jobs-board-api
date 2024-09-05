package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class ExpressionOfInterestEditor(
  private val jobRepository: JobRepository,
) {

  @Transactional
  fun createWhenNotExist(request: CreateOrDeleteExpressionOfInterestRequest): Boolean {
    var created = false

    jobRepository.findById(EntityId(request.jobId))
      .orElseThrow { IllegalArgumentException("Job not found: jobId=${request.jobId}") }
      .also { job ->
        request.prisonerPrisonNumber.let { prisonNumber ->
          job.expressionsOfInterest.computeIfAbsent(prisonNumber) {
            created = true
            ExpressionOfInterest(id = ExpressionOfInterestId(job.id, prisonNumber), job = job)
          }
        }

        if (created) {
          jobRepository.save(job)
        }
      }

    return created
  }

  @Transactional
  fun delete(request: CreateOrDeleteExpressionOfInterestRequest) {
    // FIXME to implement deleting Expression-of-Interest
    throw NotImplementedError("Deletion of Expression-of-Interest is NOT yet implemented!")
  }
}
