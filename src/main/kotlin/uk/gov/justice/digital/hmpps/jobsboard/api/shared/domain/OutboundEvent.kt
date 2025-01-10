package uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain

import java.time.Instant

data class OutboundEvent(
  val eventId: String,
  val eventType: String,
  val timestamp: Instant,
  val content: String,
)
