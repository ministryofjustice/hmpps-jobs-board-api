package uk.gov.justice.digital.hmpps.jobsboard.api.shared.application

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEvent
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEventsPublisher

class OutboundEventsService(
  val publisher: OutboundEventsPublisher,
) {
  private companion object {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun handleMessage(event: OutboundEvent) {
    log.info("handle message:  type=${event.eventType}, id=${event.eventId}")
    log.debug("handle message:  event={}", event)
    publisher.publish(event)
  }
}
