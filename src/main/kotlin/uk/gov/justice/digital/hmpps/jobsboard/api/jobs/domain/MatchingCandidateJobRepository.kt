package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse

@Repository
interface MatchingCandidateJobRepository : JpaRepository<Job, EntityId> {

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse(
      j.id.id,
      j.title,
      e.name,
      j.sector,
      j.postcode,
      j.closingDate,
      CASE WHEN eoi.createdAt IS NOT NULL THEN true ELSE false END,
      j.createdAt,
      0.0f
    )
    FROM Job j
    LEFT JOIN ExpressionOfInterest eoi ON eoi.job.id.id = j.id.id AND eoi.id.prisonNumber = :prisonNumber
    LEFT JOIN Employer e ON j.employer.id.id = e.id.id
    LEFT JOIN Archived a ON a.job.id.id = j.id.id AND a.id.prisonNumber = :prisonNumber
    WHERE (:sectors IS NULL OR LOWER(j.sector) IN :sectors)
    AND a.id IS NULL
  """,
  )
  fun findAll(
    @Param("prisonNumber") prisonNumber: String,
    @Param("sectors") sectors: List<String>?,
    pageable: Pageable,
  ): Page<GetMatchingCandidateJobsResponse>

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobDetails(
      j, :prisonNumber, eoi, a
    )
    FROM Job j
    LEFT JOIN j.expressionsOfInterest eoi on eoi.id.prisonNumber = :prisonNumber 
    LEFT JOIN j.archived a on a.id.prisonNumber = :prisonNumber
    WHERE j.id.id = :jobId
    """,
  )
  fun findJobDetailsByPrisonNumber(
    @Param("jobId") jobId: String,
    @Param("prisonNumber") prisonNumber: String,
  ): List<MatchingCandidateJobDetails>
}
