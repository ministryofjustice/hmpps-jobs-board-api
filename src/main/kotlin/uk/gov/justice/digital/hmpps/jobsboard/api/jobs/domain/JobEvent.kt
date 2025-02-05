package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import java.time.Instant

data class JobEvent(
  val eventId: String,
  val eventType: JobEventType,
  val timestamp: Instant,
  val jobId: String,
)

enum class JobEventType(val type: String, val eventTypeCode: String, val description: String) {
  JOB_CREATED(
    type = "mjma-jobs-board.job.created",
    eventTypeCode = "JobCreated",
    description = "A new Job has been created on the MJMA Jobs Board service",
  ),
  JOB_UPDATED(
    type = "mjma-jobs-board.job.updated",
    eventTypeCode = "JobCreated",
    description = "A Job has been updated on the MJMA Jobs Board service",
  ),
}
