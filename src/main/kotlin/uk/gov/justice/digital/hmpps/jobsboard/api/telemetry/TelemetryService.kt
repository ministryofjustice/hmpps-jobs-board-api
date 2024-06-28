package uk.gov.justice.digital.hmpps.jobsboard.api.telemetry

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.JobEmployerDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.EventType
import java.time.ZoneOffset

@Component
class TelemetryService(
  private val telemetryClient: TelemetryClient,
) {

  fun createAndPublishTelemetryEventMessage(JobEmployerDTO: JobEmployerDTO, eventType: EventType) {
    val logMap = createTelemetryEventMapJobCreatedUpdated(JobEmployerDTO)
  }

  fun createTelemetryEventMapJobCreatedUpdated(jobEmployerDTO: JobEmployerDTO): MutableMap<String, String> {
    val logMap: MutableMap<String, String> = HashMap()
    logMap["employerName"] = jobEmployerDTO.employerName.toString()
    logMap["userId"] = jobEmployerDTO.modifiedBy.toString()
    logMap["timestamp"] = jobEmployerDTO.modifiedDateTime?.toInstant(
      ZoneOffset.UTC,
    )!!.toString()
    return logMap
  }
}
