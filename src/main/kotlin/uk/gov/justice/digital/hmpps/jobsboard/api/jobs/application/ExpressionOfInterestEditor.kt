package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class ExpressionOfInterestEditor(
  private val jobRepository: JobRepository,
  private val expressionOfInterestRepository: ExpressionOfInterestRepository,
) {

  @Transactional
  fun createWhenNotExist(request: CreateOrDeleteExpressionOfInterestRequest): Boolean {
    var created = false
    var toBeSaved: ExpressionOfInterest

    jobRepository.findById(EntityId(request.jobId))
      .orElseThrow { IllegalArgumentException("Job not found: jobId=${request.jobId}") }
      .also { job ->
        request.prisonerPrisonNumber.let { prisonNumber ->
          toBeSaved = job.expressionsOfInterest.computeIfAbsent(prisonNumber) {
            created = true
            ExpressionOfInterest(id = ExpressionOfInterestId(job.id, prisonNumber), job = job)
          }
        }

        if (created) {
          expressionOfInterestRepository.save(toBeSaved)
        }
      }

    return created
  }

  @Transactional
  fun delete(request: CreateOrDeleteExpressionOfInterestRequest): Boolean {
    var deleted = false
    val jodId = EntityId(request.jobId)

    if (jobRepository.findById(jodId).isEmpty) {
      throw IllegalArgumentException("Job not found: jobId=${request.jobId}")
    }

    val id = ExpressionOfInterestId(jobId = jodId, prisonerPrisonNumber = request.prisonerPrisonNumber)
    if (expressionOfInterestRepository.findById(id).isPresent) {
      deleted = true
      expressionOfInterestRepository.deleteById(id)
    }
    return deleted
  }
}
