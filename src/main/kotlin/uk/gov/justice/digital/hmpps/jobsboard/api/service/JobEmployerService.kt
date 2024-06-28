package uk.gov.justice.digital.hmpps.jobsboard.api.service

import jakarta.validation.ValidationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.JobEmployer
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.JobEmployerDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobEmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.telemetry.TelemetryService
import java.util.Optional

@Service
class JobEmployerService(
  private val jobEmployerRepository: JobEmployerRepository,
  private val outboundEventsService: OutboundEventsService,
  private val telemetryService: TelemetryService,

) {
  fun getPagingList(pageNo: Int, pageSize: Int, sortBy: String): MutableList<JobEmployer>? {
    val paging: Pageable = PageRequest.of(pageNo.toInt(), pageSize.toInt(), Sort.by(sortBy))

    val pagedResult: Page<JobEmployer> = jobEmployerRepository.findAll(paging)

    if (pagedResult.hasContent()) {
      return pagedResult.getContent()
    } else {
      return ArrayList<JobEmployer>()
    }
  }
  fun createEmployer(
    jobEmployerDTO: JobEmployerDTO,
  ): JobEmployerDTO {
    var jobEmployer: JobEmployer = jobEmployerRepository.save(
      JobEmployer(jobEmployerDTO),
    )
    jobEmployerDTO.id = jobEmployer.id
    return jobEmployerDTO
  }

  fun getEmployer(
    employerId: Long,
  ): JobEmployerDTO {
    var jobEmployer: Optional<JobEmployer> = jobEmployerRepository.findById(employerId)
    if (jobEmployer.isPresent == true) {
      return JobEmployerDTO(jobEmployer.get())
    } else {
      throw ValidationException("Employer not found for employer id" + employerId)
    }
  }
}
