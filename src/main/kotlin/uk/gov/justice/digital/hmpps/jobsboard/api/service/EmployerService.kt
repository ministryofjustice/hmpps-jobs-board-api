package uk.gov.justice.digital.hmpps.jobsboard.api.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider

@Service
class EmployerService(
  private val employerRepository: EmployerRepository,
  private val timeProvider: TimeProvider,
) {

  fun existsById(employerId: String): Boolean {
    return employerRepository.existsById(EntityId(employerId))
  }

  fun save(
    request: CreateEmployerRequest,
  ) {
    employerRepository.save(
      Employer(
        id = EntityId(request.id),
        name = request.name,
        description = request.description,
        sector = request.sector,
        status = request.status,
        createdAt = timeProvider.now(),
      ),
    )
  }

  fun retrieve(id: String): Employer {
    return employerRepository.findById(EntityId(id)).orElseThrow { RuntimeException("Employer not found") }
  }

  fun getAllEmployers(name: String?, sector: String?, pageable: Pageable): Page<Employer> {
    return when {
      !name.isNullOrEmpty() && sector.isNullOrEmpty() -> employerRepository.findByName(name, pageable)
      name.isNullOrEmpty() && !sector.isNullOrEmpty() -> employerRepository.findBySector(sector, pageable)
      !name.isNullOrEmpty() && !sector.isNullOrEmpty() -> employerRepository.findByNameAndSector(name, sector, pageable)
      else -> employerRepository.findAll(pageable)
    }
  }
}
