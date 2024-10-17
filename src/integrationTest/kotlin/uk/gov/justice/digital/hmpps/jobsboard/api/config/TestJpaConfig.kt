package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.userTestName
import java.util.*

@TestConfiguration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider", auditorAwareRef = "auditorProvider")
@Profile("test-containers | test-containers-flyway")
class TestJpaConfig {
  @Primary
  @Bean
  fun dateTimeProvider(): DateTimeProvider {
    return mock(DateTimeProvider::class.java)
  }

  @Bean
  fun auditorProvider(): AuditorAware<String> {
    val auditorProvider = mock(UserPrincipalAuditorAware::class.java)
    whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(userTestName))
    return auditorProvider
  }
}
