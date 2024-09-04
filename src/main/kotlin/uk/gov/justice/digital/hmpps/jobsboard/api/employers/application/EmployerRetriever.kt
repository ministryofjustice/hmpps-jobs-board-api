package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository

@Service
class EmployerRetriever(
  private val employerRepository: EmployerRepository,
) {
  fun retrieve(id: String): Employer {
    return employerRepository.findById(EntityId(id)).orElseThrow { RuntimeException("Employer not found") }
  }

  fun retrieveAllEmployers(name: String?, sector: String?, pageable: Pageable): Page<Employer> {
    return when {
      !name.isNullOrEmpty() && sector.isNullOrEmpty() -> employerRepository.findByNameIgnoringCaseContaining(name, pageable)
      name.isNullOrEmpty() && !sector.isNullOrEmpty() -> employerRepository.findBySectorIgnoringCase(sector, pageable)
      !name.isNullOrEmpty() && !sector.isNullOrEmpty() -> employerRepository.findByNameContainingAndSectorAllIgnoringCase(name, sector, pageable)
      else -> employerRepository.findAll(pageable)
    }
  }
}
