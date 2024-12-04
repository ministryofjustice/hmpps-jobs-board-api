package uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure.ApplicationMetricsRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure.MetricsCountByStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.time.Instant

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

  @Query(
    """
    WITH recent_updates AS (
        SELECT rev_number, id, status
        FROM applications_audit aa
        WHERE aa.last_modified_at BETWEEN :startTime AND :endTime AND aa.prison_id = :prisonId AND aa.rev_type IN (0,1)
    ),
    status_at_start AS (
      SELECT aa1.rev_number, aa1.id, aa1.status FROM applications_audit aa1
      INNER JOIN (
        SELECT id, max(rev_number) as rev_number 
        FROM applications_audit aa2
        WHERE aa2.id IN (SELECT DISTINCT id from recent_updates) AND aa2.last_modified_at <= :startTime AND aa2.rev_type IN (0,1)
        GROUP BY id
      ) at_start 
      ON aa1.id = at_start.id AND aa1.rev_number = at_start.rev_number
      AND aa1.rev_number NOT IN (SELECT rev_number from recent_updates)
    ),
    application_stages AS (
        SELECT * FROM recent_updates UNION ALL SELECT * FROM status_at_start
    ),
    open_applications AS (
        SELECT DISTINCT id FROM application_stages WHERE UPPER(status) IN ('APPLICATION_MADE', 'SELECTED_FOR_INTERVIEW', 'INTERVIEW_BOOKED')
    )
    SELECT status, COUNT(DISTINCT id) as count
    FROM application_stages WHERE id IN (SELECT id FROM open_applications)
    GROUP BY status 
    """,
    nativeQuery = true,
  )
  fun countApplicationStagesByPrisonIdAndDateTimeBetween(
    prisonId: String,
    startTime: Instant,
    endTime: Instant,
  ): List<MetricsCountByStatus>

  @Query(
    """
    WITH recent_updates AS (
        SELECT rev_number, id, status
        FROM applications_audit aa
        WHERE aa.last_modified_at BETWEEN :startTime AND :endTime AND aa.prison_id = :prisonId AND aa.rev_type IN (0,1)
    ),
    status_at_start AS (
      SELECT aa1.rev_number, aa1.id, aa1.status FROM applications_audit aa1
      INNER JOIN (
        SELECT id, max(rev_number) as rev_number 
        FROM applications_audit aa2
        WHERE aa2.id IN (SELECT DISTINCT id from recent_updates) AND aa2.last_modified_at <= :startTime AND aa2.rev_type IN (0,1)
        GROUP BY id
      ) at_start 
      ON aa1.id = at_start.id AND aa1.rev_number = at_start.rev_number
      AND aa1.rev_number NOT IN (SELECT rev_number from recent_updates)
    ),
    application_stages AS (
        SELECT * FROM recent_updates UNION ALL SELECT * FROM status_at_start
    ),
    open_applications AS (
        SELECT DISTINCT id FROM application_stages WHERE UPPER(status) IN ('APPLICATION_MADE', 'SELECTED_FOR_INTERVIEW', 'INTERVIEW_BOOKED')
    ),
    application_latest AS (
        SELECT stages.* FROM application_stages stages 
        INNER JOIN (SELECT id, MAX(rev_number) as rev_number FROM application_stages GROUP BY id) latest_rev 
        ON stages.id = latest_rev.id AND stages.rev_number = latest_rev.rev_number 
        WHERE stages.id IN (SELECT id FROM open_applications) 
    )
    SELECT status, COUNT(DISTINCT id) as count
    FROM application_latest 
    GROUP BY status 
    """,
    nativeQuery = true,
  )
  fun countApplicationStatusByPrisonIdAndDateTimeBetween(
    prisonId: String,
    startTime: Instant,
    endTime: Instant,
  ): List<MetricsCountByStatus>
}
