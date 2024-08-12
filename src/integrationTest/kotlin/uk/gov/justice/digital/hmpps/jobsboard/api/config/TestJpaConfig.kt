package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@TestConfiguration
@Profile("test-containers")
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
class TestJpaConfig {
  @Bean
  fun dateTimeProvider(): DateTimeProvider {
    return mock(DateTimeProvider::class.java)
  }
}
