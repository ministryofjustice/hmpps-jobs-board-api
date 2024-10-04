package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class ExpressionOfInterestDeleter(
  private val jobRepository: JobRepository,
  private val expressionOfInterestRepository: ExpressionOfInterestRepository,
) {
  fun delete(request: DeleteExpressionOfInterestRequest) =
    expressionOfInterestRepository.deleteById(
      JobPrisonerId(
        jobId = EntityId(request.jobId),
        prisonNumber = request.prisonNumber,
      ),
    )

  fun existsById(jobId: String, prisonNumber: String): Boolean {
    if (jobRepository.findById(EntityId(jobId)).isEmpty) {
      throw IllegalArgumentException("Job not found: jobId=$jobId")
    }
    return expressionOfInterestRepository.existsById(JobPrisonerId(EntityId(jobId), prisonNumber))
  }
}
