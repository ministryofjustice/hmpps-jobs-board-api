package uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure.ApplicationMetricsRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Repository
interface ApplicationRepository :
  JpaRepository<Application, EntityId>,
  RevisionRepository<Application, EntityId, Long>,
  ApplicationMetricsRepository {

  fun findByPrisonNumberAndStatusIn(prisonNumber: String, status: List<String>, pageable: Pageable): Page<Application>

  fun findByPrisonNumberAndJobIdId(prisonNumber: String, jobId: String): List<Application>

  fun findByPrisonId(prisonId: String, pageable: Pageable): Page<Application>

  @Query(
    """
    SELECT a
    FROM Application a 
    WHERE a.prisonId = :prisonId 
    AND (
      LOWER(TRIM(CONCAT(COALESCE(a.firstName, ''), ' ', COALESCE(a.lastName, '')))) LIKE CONCAT('%', LOWER(:prisonerName), '%')
      OR :prisonerName IS NULL 
    )
    AND (
      a.status IN :status
      OR :status IS NULL 
    )
    AND (
      LOWER(a.job.title) LIKE CONCAT('%', LOWER(:jobTitleOrEmployerName), '%')
      OR LOWER(a.job.employer.name) LIKE CONCAT('%', LOWER(:jobTitleOrEmployerName), '%')
      OR :jobTitleOrEmployerName IS NULL 
    )
    """,
  )
  fun findByPrisonIdAndPrisonerNameAndApplicationStatusAndJobTitleOrEmployerName(
    @Param("prisonId") prisonId: String,
    @Param("prisonerName") prisonerName: String?,
    @Param("status") status: List<String>?,
    @Param("jobTitleOrEmployerName") jobTitleOrEmployerName: String?,
    pageable: Pageable,
  ): Page<Application>
}
