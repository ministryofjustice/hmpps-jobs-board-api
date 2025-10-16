package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider
import java.time.LocalDate

@Service
class EmployerRetriever(
  private val employerRepository: EmployerRepository,
  private val timeProvider: TimeProvider,
) {
  private val today: LocalDate get() = timeProvider.today()
  fun retrieve(id: String): Employer = employerRepository.findById(EntityId(id)).orElseThrow { RuntimeException("Employer not found") }

  fun retrieveAllEmployers(name: String?, sector: String?, pageable: Pageable, hasNationalJobs: Boolean): Page<Employer> = when {
    hasNationalJobs -> employerRepository.findEmployersWithLiveNationalJobs(today, pageable)
    !name.isNullOrEmpty() && sector.isNullOrEmpty() -> employerRepository.findByNameIgnoringCaseContaining(name, pageable)
    name.isNullOrEmpty() && !sector.isNullOrEmpty() -> employerRepository.findBySectorIgnoringCase(sector, pageable)
    !name.isNullOrEmpty() && !sector.isNullOrEmpty() -> employerRepository.findByNameContainingAndSectorAllIgnoringCase(name, sector, pageable)
    else -> employerRepository.findAll(pageable)
  }
}
