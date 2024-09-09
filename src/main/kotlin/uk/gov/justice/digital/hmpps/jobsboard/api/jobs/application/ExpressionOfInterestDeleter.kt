package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class ExpressionOfInterestDeleter(
  private val jobRepository: JobRepository,
  private val expressionOfInterestRepository: ExpressionOfInterestRepository,
) {

  @Transactional
  fun delete(request: DeleteExpressionOfInterestRequest): Boolean {
    var deleted = false
    val jodId = EntityId(request.jobId)

    if (jobRepository.findById(jodId).isEmpty) {
      throw IllegalArgumentException("Job not found: jobId=${request.jobId}")
    }

    val id = ExpressionOfInterestId(jobId = jodId, prisonNumber = request.prisonNumber)
    if (expressionOfInterestRepository.findById(id).isPresent) {
      deleted = true
      expressionOfInterestRepository.deleteById(id)
    }
    return deleted
  }
}
