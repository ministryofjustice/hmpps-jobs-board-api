package uk.gov.justice.digital.hmpps.jobsboard.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.config.TestJpaConfig
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.sainsburys
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.employerCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.employerModificationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.util.*

@DataJpaTest
@Import(TestJpaConfig::class)
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-containers")
@Transactional
class EmployerRepositoryShould {

  @Autowired
  private lateinit var dateTimeProvider: DateTimeProvider

  @Autowired
  lateinit var employerRepository: EmployerRepository

  @BeforeEach
  fun setUp() {
    employerRepository.deleteAll()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerCreationTime))
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
  fun `save an Employer`() {
    employerRepository.save(sainsburys)
  }

  @Test
  fun `find an existing Employer`() {
    employerRepository.save(sainsburys)

    assertEquals(
      Optional.of(sainsburys),
      sainsburys.id.let { employerRepository.findById(it) },
    )
  }

  @Test
  fun `not find a non existing Employer`() {
    assertFalse(employerRepository.findById(EntityId("be756fdd-8258-4561-88db-6fbd84295411")).isPresent)
  }

  @Test
  fun `set createdAt attribute when saving a new employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    assertThat(savedEmployer.createdAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `not update createdAt attribute when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerModificationTime))
    val updatedJEmployer = employerRepository.save(savedEmployer)

    assertThat(updatedJEmployer.createdAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `set modifiedAt attribute with same value as createdAt when saving a new Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    assertThat(savedEmployer.modifiedAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `update modifiedAt attribute with current date and time when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerModificationTime))
    val updatedEmployer = employerRepository.saveAndFlush(savedEmployer)

    assertThat(updatedEmployer.modifiedAt).isEqualTo(employerModificationTime)
  }
}
