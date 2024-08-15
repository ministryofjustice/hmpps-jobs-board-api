package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Repository
interface JobRepository : JpaRepository<Job, EntityId> {
  fun findByTitleOrEmployerNameIgnoringCaseContaining(
    title: String,
    employerName: String,
    pageable: Pageable,
  ): Page<Job>
}
