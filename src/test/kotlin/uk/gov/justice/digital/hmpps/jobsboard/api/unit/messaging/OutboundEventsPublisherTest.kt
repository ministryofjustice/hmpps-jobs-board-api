package uk.gov.justice.digital.hmpps.jobsboard.api.unit.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsPublisher
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.HmppsTopic
import java.lang.reflect.Modifier

@ExtendWith(MockitoExtension::class)
class OutboundEventsPublisherTest {
  private var outboundEventsPublisher: OutboundEventsPublisher? = null

  @Mock
  private val objectMapper: ObjectMapper? = null

  @Mock
  private val snsClient: SnsAsyncClient? = null

  @Mock
  private val hmppsQueueService: HmppsQueueService? = null

  @Mock
  private val hmppsDomainTopic: HmppsTopic? = null

  @Captor
  private val publishRequestArgumentCaptor: ArgumentCaptor<PublishRequest>? = null

  @BeforeEach
  fun beforeClass() {
    outboundEventsPublisher = OutboundEventsPublisher(objectMapper!!, hmppsQueueService!!)
  }

  @Disabled("Empty test")
  @Test
  fun shouldEmit_DomainEvent_ForJobCreationAndUpdate() {
  }

  @Disabled("Empty test")
  @Test
  fun shouldThrow_PublishEventException_For_Publish_Errors() {
  }

  @Disabled("Empty test")
  @Test
  fun shouldThrow_PublishEventException_For_TOPIC_NOT_FOUND() {
  }

  fun Any.mockPrivateFields(vararg mocks: Any): Any {
    mocks.forEach { mock ->
      javaClass.declaredFields
        .filter { it.modifiers.and(Modifier.PRIVATE) > 0 || it.modifiers.and(Modifier.PROTECTED) > 0 }
        .firstOrNull { it.type == mock.javaClass }
        ?.also { it.isAccessible = true }
        ?.set(this, mock)
    }
    return this
  }
}
