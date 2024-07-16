package uk.gov.justice.digital.hmpps.jobsboard.api.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Repository
interface EmployerRepository : JpaRepository<Employer, EntityId> {
  fun findByName(name: String?, pageable: Pageable): Page<Employer>
  fun findBySector(sector: String?, pageable: Pageable): Page<Employer>
  fun findByNameAndSector(name: String?, sector: String?, pageable: Pageable): Page<Employer>
}
