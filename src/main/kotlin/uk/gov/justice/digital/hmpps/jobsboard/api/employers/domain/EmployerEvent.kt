package uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain

import java.time.Instant

data class EmployerEvent(
  val eventId: String,
  val eventType: EmployerEventType,
  val timestamp: Instant,
  val employerId: String,
)

enum class EmployerEventType(val eventTypeCode: String) {
  CREATED("EmployerCreated"),
  UPDATED("EmployerUpdated"),
}
