package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ExpressionOfInterestRepository : JpaRepository<ExpressionOfInterest, JobPrisonerId> {
  fun findByIdPrisonNumberOrderByCreatedAtDesc(prisonNumber: String): List<ExpressionOfInterest>

  fun findByIdPrisonNumberAndCreatedAtBetweenOrderByCreatedAtDesc(
    prisonNumber: String,
    start: Instant?,
    end: Instant?,
  ): List<ExpressionOfInterest>

  fun findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
    prisonNumber: String,
    end: Instant,
  ): List<ExpressionOfInterest>

  fun findByIdPrisonNumberAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
    prisonNumber: String,
    start: Instant,
  ): List<ExpressionOfInterest>
}
