package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ArchivedRepository : JpaRepository<Archived, JobPrisonerId> {
  fun findByIdPrisonNumberOrderByCreatedAtDesc(prisonNumber: String): List<Archived>

  fun findByIdPrisonNumberAndCreatedAtBetweenOrderByCreatedAtDesc(
    prisonNumber: String,
    start: Instant?,
    end: Instant?,
  ): List<Archived>

  fun findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
    prisonNumber: String,
    end: Instant,
  ): List<Archived>

  fun findByIdPrisonNumberAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
    prisonNumber: String,
    start: Instant,
  ): List<Archived>
}
