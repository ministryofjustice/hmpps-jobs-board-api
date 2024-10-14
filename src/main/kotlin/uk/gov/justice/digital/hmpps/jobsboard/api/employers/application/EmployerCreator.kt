package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.exceptions.NotFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

@Service
class EmployerCreator(
  private val employerRepository: EmployerRepository,
) {
  fun create(request: CreateEmployerRequest) {
    val jobs = emptyList<Job>()
    save(request, jobs)
  }

  fun update(request: CreateEmployerRequest) {
    val employer = employerRepository.findById(EntityId(request.id))
      .orElseThrow { NotFoundException("Employer not found: employerId = ${request.id}") }
    save(request, employer.jobs)
  }

  fun existsById(employerId: String): Boolean {
    return employerRepository.existsById(EntityId(employerId))
  }

  private fun save(request: CreateEmployerRequest, jobs: List<Job>) {
    employerRepository.save(
      Employer(
        id = EntityId(request.id),
        name = request.name,
        description = request.description,
        sector = request.sector,
        status = request.status,
        jobs = jobs,
      ),
    )
  }
}
