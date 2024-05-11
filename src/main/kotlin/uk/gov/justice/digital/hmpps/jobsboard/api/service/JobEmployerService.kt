package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobEmployer
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobEmployerDTO
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository.JobEmployerRepository
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.telemetry.TelemetryService
import java.time.LocalDateTime

@Service
class JobEmployerService(
  private val jobEmployerRepository: JobEmployerRepository,
  private val outboundEventsService: OutboundEventsService,
  private val telemetryService: TelemetryService,
) {
  fun createEmployer(
    jobsProfileDTO: JobEmployerDTO,
  ): JobEmployerDTO {
    var jobEmployer: JobEmployer = jobEmployerRepository.save(
      JobEmployer(
        jobsProfileDTO.id,
        jobsProfileDTO.employerName,
        jobsProfileDTO.employerBio,
        "sacintha",
        LocalDateTime.now(),
        "sacintha",
        LocalDateTime.now(),
        jobsProfileDTO.sector,
        jobsProfileDTO.partner,
        jobsProfileDTO.image,
        jobsProfileDTO.postCode,
      ),
    )
    jobsProfileDTO.id = jobEmployer.id
    return jobsProfileDTO
  }
}
