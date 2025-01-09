package uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain

import java.time.Instant

data class EmployerEvent(
  val eventId: String,
  val eventType: EmployerEventType,
  val timestamp: Instant,
  val employerId: String,
)

enum class EmployerEventType(val type: String, val eventTypeCode: String, val description: String) {
  EMPLOYER_CREATED(
    type = "mjma-jobs-board.employer.created",
    eventTypeCode = "EmployerCreated",
    description = "A new employer has been created on the MJMA Jobs Board service",
  ),
  EMPLOYER_UPDATED(
    type = "mjma-jobs-board.employer.updated",
    eventTypeCode = "EmployerUpdated",
    description = "An employer has been updated on the MJMA Jobs Board service",
  ),
}
