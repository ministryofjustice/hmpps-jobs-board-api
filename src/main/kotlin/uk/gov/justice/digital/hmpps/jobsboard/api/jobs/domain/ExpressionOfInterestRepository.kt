package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ExpressionOfInterestRepository : JpaRepository<ExpressionOfInterest, JobPrisonerId> {
  fun findByIdPrisonNumber(prisonNumber: String): List<ExpressionOfInterest>

  @Query(
    """
    SELECT e FROM ExpressionOfInterest e
    WHERE e.id.prisonNumber = :prisonNumber
    AND e.createdAt >= :fromDate
    AND e.createdAt <= :toDate
    ORDER BY e.createdAt DESC
  """,
  )
  fun findByPrisonNumberAndDateBetween(
    @Param("prisonNumber") prisonNumber: String,
    @Param("fromDate") fromDate: Instant?,
    @Param("toDate") toDate: Instant?,
  ): List<ExpressionOfInterest>
}
