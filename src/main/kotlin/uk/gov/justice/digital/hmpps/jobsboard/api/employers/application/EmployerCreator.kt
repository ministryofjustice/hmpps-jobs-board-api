package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Service
class EmployerCreator(
  private val employerRepository: EmployerRepository,
) {
  fun createOrUpdate(
    request: CreateEmployerRequest,
  ) {
    employerRepository.save(
      Employer(
        id = EntityId(request.id),
        name = request.name,
        description = request.description,
        sector = request.sector,
        status = request.status,
      ),
    )
  }

  fun existsById(employerId: String): Boolean {
    return employerRepository.existsById(EntityId(employerId))
  }
}
