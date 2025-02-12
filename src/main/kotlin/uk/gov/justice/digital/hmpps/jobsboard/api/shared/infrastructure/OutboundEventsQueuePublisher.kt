package uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEvent
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEventsPublisher
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.MissingQueueException
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue as SqsMessageAttributeValue

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
        .messageAttributes(event.messageAttributes())
        .build()
        .also { log.debug("Send event {} to outbound queue", event.eventId) },
    ).get().let {
      log.info("Sent event ${event.eventId} to outbound queue")
    }
  }

  private fun OutboundEvent.messageAttributes() = eventAttributesToMessageAttributes(eventType, eventId)

  private fun eventAttributesToMessageAttributes(
    eventType: String,
    eventId: String? = null,
    noTracing: Boolean = false,
  ): Map<String, SqsMessageAttributeValue> = buildMap {
    put("eventType", attributeValue(eventType))
    eventId?.let { put("eventId", attributeValue(it)) }
    if (noTracing) put("noTracing", attributeValue("true"))
  }

  private fun attributeValue(value: String) = SqsMessageAttributeValue.builder().dataType("String").stringValue(value).build()
}
