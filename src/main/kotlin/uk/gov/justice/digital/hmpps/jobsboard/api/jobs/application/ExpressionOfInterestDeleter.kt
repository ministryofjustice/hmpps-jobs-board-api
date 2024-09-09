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
  fun delete(request: DeleteExpressionOfInterestRequest) =
    expressionOfInterestRepository.deleteById(
      ExpressionOfInterestId(
        jobId = EntityId(request.jobId),
        prisonNumber = request.prisonNumber,
      ),
    )

  fun existsById(jobId: String, prisonNumber: String): Boolean {
    if (jobRepository.findById(EntityId(jobId)).isEmpty) {
      throw IllegalArgumentException("Job not found: jobId=$jobId")
    }
    return expressionOfInterestRepository.existsById(ExpressionOfInterestId(EntityId(jobId), prisonNumber))
  }
}
