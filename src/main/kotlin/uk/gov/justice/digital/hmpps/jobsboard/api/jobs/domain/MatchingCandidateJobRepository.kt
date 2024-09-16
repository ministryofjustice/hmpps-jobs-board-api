package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Repository
interface MatchingCandidateJobRepository : JpaRepository<Job, EntityId> {
  fun findBySectorInIgnoringCase(sectors: List<String>, pageable: Pageable): Page<Job>

  @Query(
    """
    SELECT j, eoi, a from Job j 
    LEFT JOIN j.expressionsOfInterest eoi on eoi.id.prisonNumber = :prisonNumber 
    LEFT JOIN j.archived a on a.id.prisonNumber = :prisonNumber
    WHERE j.id.id = :jobId
    """,
  )
  fun findJobDetailsByPrisonNumber(
    @Param("jobId") jobId: String,
    @Param("prisonNumber") prisonNumber: String,
  ): List<Array<Any>>
}
