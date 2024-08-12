package uk.gov.justice.digital.hmpps.jobsboard.api

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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.config.TestJpaConfig
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month.JULY
import java.util.*

@DataJpaTest
@Import(TestJpaConfig::class)
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-containers")
@Transactional
class JobRepositoryShould {

  @Autowired
  private lateinit var dateTimeProvider: DateTimeProvider

  @Autowired
  private lateinit var employerRepository: EmployerRepository

  @Autowired
  private lateinit var jobRepository: JobRepository

  private val employerFixedTime = LocalDateTime.of(2024, JULY, 20, 22, 6)
  private val jobCreationTime = Instant.parse("2024-01-01T00:00:00Z")
  private val jobModificationTime = Instant.parse("2025-02-02T01:00:00Z")

  private val amazonEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Amazon",
    description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
    sector = "LOGISTICS",
    status = "KEY_PARTNER",
    createdAt = employerFixedTime,
  )

  private val amazonForkliftOperatorJob = Job(
    id = EntityId("fe5d5175-5a21-4cec-a30b-fd87a5f76ce7"),
    title = "Forklift operator",
    sector = "WAREHOUSING",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "",
    postCode = "LS12",
    salaryFrom = 11.93f,
    salaryTo = 15.90f,
    salaryPeriod = "PER_HOUR",
    additionalSalaryInformation = "",
    isPayingAtLeastNationalMinimumWage = false,
    workPattern = "FLEXIBLE_SHIFTS",
    hoursPerWeek = "FULL_TIME",
    contractType = "TEMPORARY",
    baseLocation = "WORKPLACE",
    essentialCriteria = "",
    desirableCriteria = "",
    description = """
      What's on offer:

      - 5 days over 7, 05:30 to 15:30
      - Paid weekly
      - Immediate starts available
      - Full training provided

      Your duties will include:

      - Manoeuvring forklifts safely in busy industrial environments
      - Safely stacking and unstacking large quantities of goods onto shelves or pallets
      - Moving goods from storage areas to loading areas for transport
      - Unloading deliveries and safely relocating the goods to their designated storage areas
      - Ensuring forklift driving areas are free from spills or obstructions
      - Regularly checking forklift equipment for faults or damages
      - Consolidating partial pallets for incoming goods
    """.trimIndent(),
    offenceExclusions = "NONE,DRIVING",
    isRollingOpportunity = false,
    closingDate = LocalDate.of(2024, JULY, 20),
    isOnlyForPrisonLeavers = true,
    startDate = LocalDate.of(2024, JULY, 20),
    howToApply = "",
    supportingDocumentationRequired = "CV,DISCLOSURE_LETTER",
    supportingDocumentationDetails = "",
    employer = amazonEmployer,
  )

  @BeforeEach
  fun setUp() {
    jobRepository.deleteAll()
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
    employerRepository.save(amazonEmployer)
    jobRepository.save(amazonForkliftOperatorJob)
  }

  @Test
  fun `set createdAt attribute when saving a new Job`() {
    employerRepository.save(amazonEmployer)
    val savedJob = jobRepository.save(amazonForkliftOperatorJob)

    assertThat(savedJob.createdAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `set modifiedAt attribute with same value as createdAt when saving a new Job`() {
    employerRepository.save(amazonEmployer)
    val savedJob = jobRepository.save(amazonForkliftOperatorJob)

    assertThat(savedJob.modifiedAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `update modifiedAt attribute with current date and time when updating an existing Job`() {
    employerRepository.save(amazonEmployer)
    jobRepository.save(amazonForkliftOperatorJob)

    whenever(dateTimeProvider.getNow()).thenReturn(Optional.of(jobModificationTime))
    val updatedJob = jobRepository.saveAndFlush(amazonForkliftOperatorJob)

    assertThat(updatedJob.modifiedAt).isEqualTo(jobModificationTime)
  }
}
