package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEventsPublisher
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure.OutboundEventsQueuePublisher
import uk.gov.justice.hmpps.sqs.HmppsQueueService

@Configuration
@EnableConfigurationProperties(IntegrationProperties::class)
@ConditionalOnProperty("api.integration.enabled", havingValue = "true")
class IntegrationConfiguration {
  @Bean
  fun outboundEventsPublisher(
    hmppsQueueService: HmppsQueueService,
    apiIntegrationProperties: IntegrationProperties,
  ): OutboundEventsPublisher = OutboundEventsQueuePublisher(
    hmppsQueueService,
    queueId = "outboundintegrationqueue",
  )

  @Bean
  fun outboundEventsService(outboundEventsPublisher: OutboundEventsPublisher) = OutboundEventsService(outboundEventsPublisher)
}
