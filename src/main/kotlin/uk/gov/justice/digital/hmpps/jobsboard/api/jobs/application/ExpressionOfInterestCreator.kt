package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository

@Service
class ExpressionOfInterestCreator(
  private val jobRepository: JobRepository,
  private val expressionOfInterestRepository: ExpressionOfInterestRepository,
) {

  @Transactional
  fun createOrUpdate(request: CreateExpressionOfInterestRequest) {
    val job = jobRepository.findById(EntityId(request.jobId))
      .orElseThrow { IllegalArgumentException("Job not found: jobId=${request.jobId}") }

    val expressionOfInterest = ExpressionOfInterest(
      id = ExpressionOfInterestId(job.id, request.prisonNumber),
      job = job,
    )

    expressionOfInterestRepository.save(expressionOfInterest)
  }

  fun existsById(jobId: String, prisonNumber: String): Boolean {
    if (jobRepository.findById(EntityId(jobId)).isEmpty) {
      throw IllegalArgumentException("Job not found: jobId=$jobId")
    }
    return expressionOfInterestRepository.existsById(ExpressionOfInterestId(EntityId(jobId), prisonNumber))
  }
}
