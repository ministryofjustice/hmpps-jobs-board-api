package uk.gov.justice.digital.hmpps.jobsboard.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test-containers")
class EmployerRepositoryShould {

  @Autowired
  lateinit var jobEmployerRepository: EmployerRepository

  companion object {
    private val postgresContainer = PostgresContainer.instance

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
    val employer = Employer(
      id = EntityId("be756fdd-8258-4561-88db-6fbd84295410"),
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      createdBy = "Sacintha",
      createdWhen = LocalDateTime.parse("2024-05-16T11:15:04.915205"),
      modifiedBy = "Javier",
      modifiedWhen = LocalDateTime.parse("2024-05-16T11:16:04.915205"),
      sector = "sector",
      status = "status",
    )

    jobEmployerRepository.save(employer)
  }

  @Test
  fun `find an existing Employer`() {
    val employer = Employer(
      id = EntityId("be756fdd-8258-4561-88db-6fbd84295410"),
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      createdBy = "Sacintha",
      createdWhen = LocalDateTime.parse("2024-05-16T11:15:04.915205"),
      modifiedBy = "Javier",
      modifiedWhen = LocalDateTime.parse("2024-05-16T11:16:04.915205"),
      sector = "sector",
      status = "status",
    )

    jobEmployerRepository.save(employer)

    assertEquals(
      Optional.of(employer),
      employer.id.let { jobEmployerRepository.findById(it) },
    )
  }

  @Test
  fun `not find a non existing Employer`() {
    assertFalse(jobEmployerRepository.findById(EntityId("be756fdd-8258-4561-88db-6fbd84295411")).isPresent)
  }
}
