package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobModificationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.userTestName
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.util.*

@DataJpaTest
@Import(TestJpaConfig::class)
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-containers")
class JobRepositoryShould {
  @Autowired
  private lateinit var auditorProvider: AuditorAware<String>

  @Autowired
  private lateinit var dateTimeProvider: DateTimeProvider

  @Autowired
  private lateinit var employerRepository: EmployerRepository

  @Autowired
  private lateinit var jobRepository: JobRepository

  @BeforeEach
  fun setUp() {
    jobRepository.deleteAll()
    employerRepository.deleteAll()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobCreationTime))
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

  @Test
  fun `save a new Job`() {
    employerRepository.save(amazon)
    jobRepository.save(amazonForkliftOperator)
  }

  @Test
  fun `set createdAt attribute when saving a new Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.createdAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `not update createdAt attribute when updating an existing Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.save(amazonForkliftOperator)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobModificationTime))

    val updatedJob = jobRepository.save(savedJob)

    assertThat(updatedJob.createdAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `set modifiedAt attribute with same value as createdAt when saving a new Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.modifiedAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `update modifiedAt attribute with current date and time when updating an existing Job`() {
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobModificationTime))
    employerRepository.save(amazon)
    jobRepository.save(amazonForkliftOperator)

    val updatedJob = jobRepository.saveAndFlush(amazonForkliftOperator)

    assertThat(updatedJob.modifiedAt).isEqualTo(jobModificationTime)
  }

  @Test
  fun `set createdBy attribute when saving a new Job`() {
    employerRepository.save(amazon)

    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.createdBy).isEqualTo(userTestName)
  }

  @Test
  fun `set lastModifiedBy attribute when saving a new Job`() {
    employerRepository.save(amazon)

    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.lastModifiedBy).isEqualTo(userTestName)
  }
}
