package uk.gov.justice.digital.hmpps.jobsboard.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-containers")
@Transactional
class EmployerRepositoryShould {

  @Autowired
  lateinit var employerRepository: EmployerRepository

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
        createdAt = LocalDateTime.parse("2024-05-16T11:15:04.915205"),
      )
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
}
