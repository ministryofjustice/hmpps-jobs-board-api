package uk.gov.justice.digital.hmpps.jobsboard.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
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
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours.JOB_SHARE
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours.ZERO_HOURS
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod.PER_DAY
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork.BEAUTY
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test-containers")
class JobRepositoryShould {

  @Autowired
  lateinit var jobEmployerRepository: EmployerRepository

  @Autowired
  lateinit var jobRepository: JobRepository

  private lateinit var employer: Employer
  private lateinit var job: Job

  @BeforeEach
  fun setUp() {
    employer = Employer(
      id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      sector = "sector",
      status = "status",
      createdAt = LocalDateTime.now(),
    )
    job = Job(
      id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
      employer = employer,
      salaryPeriodName = PER_DAY,
      workPatternName = JOB_SHARE,
      sectorName = "food industry",
      hoursName = ZERO_HOURS,
      additionalSalaryInformation = "Salary will be credited at the end of each shift",
      desirableJobCriteria = "Candidate should be proactive and prgmatic",
      essentialJobCriteria = "Need some one with techincal abilities at a beginer level in computer",
      closingDate = LocalDateTime.now().plusDays(20),
      howToApply = "Apply through Website",
      jobTitle = "Java developer",
      createdBy = "Sacintha",
      createdDateTime = LocalDateTime.now(),
      postingDate = LocalDateTime.now().plusDays(5).toString(),
      deletedBy = "Sacintha",
      deletedDateTime = LocalDateTime.now().plusDays(5),
      modifiedBy = "Sacintha",
      modifiedDateTime = LocalDateTime.now(),
      nationalMinimumWage = true,
      postCode = "EH7 5HH",
      city = "Edinburgh",
      ringFencedJob = true,
      rollingJobOppurtunity = true,
      activeJob = true,
      deletedJob = false,
      salaryFrom = "10£",
      salaryTo = "15£",
      typeOfWork = BEAUTY,
      distance = 0,
    )
  }

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
    jobEmployerRepository.save(employer)
  }

  @Test
  fun `save a Job`() {
    jobEmployerRepository.save(employer)
    jobRepository.save(job)
  }

  @Test
  fun `find an existing job`() {
    jobEmployerRepository.save(employer)
    jobRepository.save(job)

    assertEquals(
      Optional.of(job),
      job.id.let { jobRepository.findById(it) },
    )
  }

  @Test
  fun `not find a non existing job`() {
    assertFalse(jobRepository.findById(EntityId("be756fdd-8258-4561-88db-6fbd84295433")).isPresent)
  }
}
