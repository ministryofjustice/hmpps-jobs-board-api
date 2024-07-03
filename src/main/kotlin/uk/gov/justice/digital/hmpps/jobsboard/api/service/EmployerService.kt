package uk.gov.justice.digital.hmpps.jobsboard.api.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository

@Service
class EmployerService(
  private val jobEmployerRepository: EmployerRepository,
) {

  fun createEmployer(
    request: CreateEmployerRequest,
  ) {
    jobEmployerRepository.save(
      Employer(
        id = EntityId(request.id),
        name = request.name,
        description = request.description,
        createdAt = null,
        sector = request.sector,
        status = request.status,
      ),
    )
  }

  fun retrieveEmployer(id: String): Employer {
    return jobEmployerRepository.findById(EntityId(id)).orElseThrow { RuntimeException("Employer not found") }
  }

  fun getPagingList(pageNo: Int, pageSize: Int, sortBy: String): MutableList<Employer>? {
    val paging: Pageable = PageRequest.of(pageNo.toInt(), pageSize.toInt(), Sort.by(sortBy))

    val pagedResult: Page<Employer> = jobEmployerRepository.findAll(paging)

    if (pagedResult.hasContent()) {
      return pagedResult.getContent()
    } else {
      return ArrayList<Employer>()
    }
  }
}
