package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import uk.gov.justice.hmpps.sqs.HmppsQueueFactory
import uk.gov.justice.hmpps.sqs.HmppsSqsProperties
import uk.gov.justice.hmpps.sqs.MissingQueueException

const val OUTBOUND_QUEUE_ID = "outboundintegrationqueue"

@TestConfiguration
class SqsTestConfig(private val hmppsQueueFactory: HmppsQueueFactory) {

  @Bean("outboundintegrationqueue-sqs-client")
  fun outboundQueueSqsClient(
    hmppsSqsProperties: HmppsSqsProperties,
  ): SqsAsyncClient = with(hmppsSqsProperties) {
    val config = queues[OUTBOUND_QUEUE_ID]
      ?: throw MissingQueueException("HmppsSqsProperties config for $OUTBOUND_QUEUE_ID not found")
    hmppsQueueFactory.createSqsAsyncClient(config, hmppsSqsProperties, null)
  }
}
