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
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.config.TestJpaConfig
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.time.Instant
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

  val employerCreationTime = Instant.parse("2024-07-01T01:00:00Z")
  val employerModificationTime = Instant.parse("2024-07-01T02:00:00Z")

  private lateinit var employer: Employer

  @BeforeEach
  fun setUp() {
    employerRepository.deleteAll()
    employer =
      Employer(
        id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
        name = "Sainsbury's",
        description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
        sector = "sector",
        status = "status",
      )
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
    employerRepository.save(employer)
  }

  @Test
  fun `find an existing Employer`() {
    employerRepository.save(employer)

    assertEquals(
      Optional.of(employer),
      employer.id.let { employerRepository.findById(it) },
    )
  }

  @Test
  fun `not find a non existing Employer`() {
    assertFalse(employerRepository.findById(EntityId("be756fdd-8258-4561-88db-6fbd84295411")).isPresent)
  }

  @Test
  fun `set createdAt attribute when saving a new employer`() {
    val savedEmployer = employerRepository.save(employer)

    assertThat(savedEmployer.createdAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `not update createdAt attribute when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(employer)

    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerModificationTime))
    val updatedJEmployer = employerRepository.save(savedEmployer)

    assertThat(updatedJEmployer.createdAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `set modifiedAt attribute with same value as createdAt when saving a new Employer`() {
    val savedEmployer = employerRepository.save(employer)

    assertThat(savedEmployer.modifiedAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `update modifiedAt attribute with current date and time when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(employer)

    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerModificationTime))
    val updatedEmployer = employerRepository.saveAndFlush(savedEmployer)

    assertThat(updatedEmployer.modifiedAt).isEqualTo(employerModificationTime)
  }
}
