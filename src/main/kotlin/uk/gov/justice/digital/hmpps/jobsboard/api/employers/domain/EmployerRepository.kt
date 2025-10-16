package uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.time.LocalDate

@Repository
interface EmployerRepository : JpaRepository<Employer, EntityId> {
  fun findByNameIgnoringCaseContaining(name: String, pageable: Pageable): Page<Employer>
  fun findBySectorIgnoringCase(sector: String, pageable: Pageable): Page<Employer>
  fun findByNameContainingAndSectorAllIgnoringCase(name: String?, sector: String?, pageable: Pageable): Page<Employer>
  fun countByNameIgnoreCaseAndIdNot(name: String, id: EntityId): Long

  @Query(
    """
    SELECT e
    FROM Employer e
    JOIN Job j on j.employer.id.id = e.id.id
    WHERE (j.closingDate IS NULL OR j.closingDate >= :currentDate) and j.isNational = true
    GROUP BY e.id
  """,
  )
  fun findEmployersWithLiveNationalJobs(
    @Param("currentDate") currentDate: LocalDate,
    pageable: Pageable,
  ): Page<Employer>
}
