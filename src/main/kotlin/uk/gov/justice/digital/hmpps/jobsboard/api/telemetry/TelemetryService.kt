package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.telemetry

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobEmployer
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging.EventType
import java.time.ZoneOffset

@Component
class TelemetryService(
  private val telemetryClient: TelemetryClient,
) {

  fun createAndPublishTelemetryEventMessage(jobsBoardProfile: JobEmployer, eventType: EventType) {
    val logMap = createTelemetryEventMap_job_created_updated(jobsBoardProfile)
  }

  fun createTelemetryEventMap_job_created_updated(jobsBoardProfile: JobEmployer): MutableMap<String, String> {
    val logMap: MutableMap<String, String> = HashMap()
    logMap["employerName"] = jobsBoardProfile.employerName.toString()
    logMap["userId"] = jobsBoardProfile.modifiedBy.toString()
    logMap["timestamp"] = jobsBoardProfile.modifiedDateTime?.toInstant(
      ZoneOffset.UTC,
    )!!.toString()
    return logMap
  }
}
