package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId

@Repository
interface JobRepository : JpaRepository<Job, EntityId> {
  fun findByTitleContainingOrEmployerNameContainingAllIgnoringCase(
    title: String,
    employerName: String,
    pageable: Pageable,
  ): Page<Job>

  fun findBySectorIgnoringCase(sector: String, pageable: Pageable): Page<Job>

  @Query(
    """
    SELECT j FROM Job j
    WHERE LOWER(j.sector) = LOWER(:sector)
    AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :searchString, '%'))
        OR LOWER(j.employer.name) LIKE LOWER(CONCAT('%', :searchString, '%'))
    )
  """,
  )
  fun findBySectorAndTitleOrEmployerName(
    @Param("searchString") searchString: String,
    @Param("sector") sector: String,
    pageable: Pageable,
  ): Page<Job>
}
