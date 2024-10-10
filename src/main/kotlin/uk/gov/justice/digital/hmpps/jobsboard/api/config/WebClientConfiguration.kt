package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(
  @Value("\${os.places.api.url}") private val osPlacesApiUrl: String,
) {
  @Bean
  fun osPlacesClient(): WebClient = WebClient.builder()
    .baseUrl(osPlacesApiUrl)
    .exchangeStrategies(
      ExchangeStrategies.builder()
        .codecs { configurer ->
          configurer.defaultCodecs()
            .maxInMemorySize(-1)
        }
        .build(),
    ).build()
}
