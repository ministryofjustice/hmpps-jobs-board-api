package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.telemetry

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobsBoardProfile
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging.EventType
import java.time.ZoneOffset

@Component
class TelemetryService(
  private val telemetryClient: TelemetryClient,
) {

  fun createAndPublishTelemetryEventMessage(jobsBoardProfile: JobsBoardProfile, eventType: EventType) {
    val logMap = createTelemetryEventMap_job_created_updated(jobsBoardProfile)
  }

  fun createTelemetryEventMap_job_created_updated(jobsBoardProfile: JobsBoardProfile): MutableMap<String, String> {
    val logMap: MutableMap<String, String> = HashMap()
    logMap["prisonId"] = jobsBoardProfile.prisonId.toString()
    logMap["userId"] = jobsBoardProfile.modifiedBy.toString()
    logMap["timestamp"] = jobsBoardProfile.modifiedDateTime?.toInstant(
      ZoneOffset.UTC,
    )!!.toString()
    return logMap
  }
}
