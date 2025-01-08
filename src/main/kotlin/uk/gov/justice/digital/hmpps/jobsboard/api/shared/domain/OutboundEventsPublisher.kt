package uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain

interface OutboundEventsPublisher {
  fun publish(event: OutboundEvent)
}
