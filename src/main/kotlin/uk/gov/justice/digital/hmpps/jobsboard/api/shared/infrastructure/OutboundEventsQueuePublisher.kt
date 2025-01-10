package uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEvent
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEventsPublisher
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.MissingQueueException
import uk.gov.justice.hmpps.sqs.eventTypeMessageAttributes

/***
 * OutboundEventsQueuePublisher
 *
 * This "queue publisher" is using `Producer-Consumer` pattern instead of `Pub/Sub`
 */
class OutboundEventsQueuePublisher(
  private val hmppsQueueService: HmppsQueueService,
  val queueId: String,
) : OutboundEventsPublisher {
  private companion object {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  private val outboundQueue =
    hmppsQueueService.findByQueueId(queueId) ?: throw MissingQueueException("Could not find queue $queueId")

  override fun publish(event: OutboundEvent) {
    outboundQueue.sqsClient.sendMessage(
      SendMessageRequest.builder()
        .queueUrl(outboundQueue.queueUrl)
        .messageBody(event.content)
        .eventTypeMessageAttributes(event.eventType)
        .build()
        .also { log.debug("Send event {} to outbound queue", event.eventId) },
    ).get().let {
      log.info("Sent event ${event.eventId} to outbound queue")
    }
  }
}
