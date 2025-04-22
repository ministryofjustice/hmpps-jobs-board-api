package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ArchivedRepository : JpaRepository<Archived, JobPrisonerId> {

  fun findByIdPrisonNumber(prisonNumber: String): List<Archived>

  @Query(
    """
    SELECT a FROM Archived a
    WHERE a.id.prisonNumber = :prisonNumber
    AND a.createdAt >= :fromDate
    AND a.createdAt <= :toDate
    ORDER BY a.createdAt DESC
  """,
  )
  fun findByPrisonNumberAndDateBetween(
    @Param("prisonNumber") prisonNumber: String,
    @Param("fromDate") fromDate: Instant?,
    @Param("toDate") toDate: Instant?,
  ): List<Archived>
}
