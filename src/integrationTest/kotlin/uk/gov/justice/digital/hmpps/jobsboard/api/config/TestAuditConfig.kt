package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.context.annotation.Bean
import org.springframework.data.auditing.DateTimeProvider
import java.time.Instant
import java.util.*

class TestAuditConfig {
  @Bean
  fun dateTimeProvider(): DateTimeProvider {
    return DateTimeProvider { Optional.of(Instant.parse("2024-01-01T00:00:00Z")) }
  }
}
