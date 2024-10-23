package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(
  private val osPlacesAPIProperties: OsPlacesApiProperties,
) {
  @Bean
  fun osPlacesClient(): WebClient = WebClient.builder()
    .baseUrl(osPlacesAPIProperties.url)
    .exchangeStrategies(
      ExchangeStrategies.builder()
        .codecs { configurer ->
          configurer.defaultCodecs()
            .maxInMemorySize(-1)
        }
        .build(),
    ).build()
}
