package uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import uk.gov.justice.digital.hmpps.jobsboard.api.config.TestJpaConfig
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.userTestName
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.time.Instant
import java.util.*

@DataJpaTest
@Import(TestJpaConfig::class)
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-containers")
abstract class RepositoryTestCase {
  @Autowired
  protected lateinit var dateTimeProvider: DateTimeProvider

  @Autowired
  protected lateinit var auditorProvider: AuditorAware<String>

  @Autowired
  protected lateinit var entityManager: EntityManager

  @Autowired
  protected lateinit var employerRepository: EmployerRepository

  @Autowired
  protected lateinit var jobRepository: JobRepository

  private final val defaultCreationTime: Instant = TestPrototypes.defaultCreationTime

  @BeforeEach
  fun setUp() {
    jobRepository.deleteAll()
    employerRepository.deleteAll()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(defaultCreationTime))
    whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(userTestName))
  }

  companion object {
    private val postgresContainer = PostgresContainer.repositoryContainer

    @JvmStatic
    @DynamicPropertySource
    fun configureTestContainers(registry: DynamicPropertyRegistry) {
      postgresContainer?.run {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
        registry.add("spring.datasource.username", postgresContainer::getUsername)
        registry.add("spring.datasource.password", postgresContainer::getPassword)
      }
    }
  }
}
