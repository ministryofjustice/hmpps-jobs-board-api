package uk.gov.justice.digital.hmpps.jobsboard.api.service

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

  fun getEmployers(): List<Employer> {
    return employerRepository.findAll()
  }
}
