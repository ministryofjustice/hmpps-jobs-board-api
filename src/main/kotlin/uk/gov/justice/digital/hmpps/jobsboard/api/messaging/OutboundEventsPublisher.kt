package uk.gov.justice.digital.hmpps.jobsboard.api.messaging
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import java.util.function.Supplier

class OutboundEventsPublisher(
  private val objectMapper: ObjectMapper,
  private val hmppsQueueService: HmppsQueueService,
) {
  companion object {
    const val TOPIC_ID = "domainevents"
    const val EVENT_TYPE_KEY = "eventType"
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  private val hmppsDomainTopic by lazy {
    hmppsQueueService.findByTopicId(TOPIC_ID) ?: throw IllegalStateException("domainevents not found")
  }
  private val topicArn by lazy { hmppsDomainTopic.arn }
  private val topicSnsClient by lazy { hmppsDomainTopic.snsClient }

  fun publishToTopic(outboundEvent: OutboundEvent) {
    try {
      topicSnsClient.publish(
        PublishRequest.builder()
          .topicArn(hmppsDomainTopic.arn)
          .message(objectMapper.writeValueAsString(outboundEvent))
          .messageAttributes(
            mapOf(
              "eventType" to MessageAttributeValue.builder().dataType("String").stringValue(outboundEvent.eventType.eventType).build(),
            ),
          )
          .build()
          .also { log.info("Published event $outboundEvent to outbound topic") },
      )
    } catch (e: Throwable) {
      val message = "Failed (publishToTopic) to publish Event $outboundEvent.eventType to $TOPIC_ID"
      log.error(message, e)
      throw PublishEventException(message, e)
    }
  }
}
class PublishEventException(message: String? = null, cause: Throwable? = null) :
  RuntimeException(message, cause),
  Supplier<PublishEventException> {
  override fun get(): PublishEventException = PublishEventException(message, cause)
}
