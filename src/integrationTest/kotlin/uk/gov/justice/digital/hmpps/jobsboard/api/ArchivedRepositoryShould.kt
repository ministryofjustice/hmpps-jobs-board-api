package uk.gov.justice.digital.hmpps.jobsboard.api

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataAccessException
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.config.TestJpaConfig
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import java.time.Instant
import java.time.LocalDate
import java.time.Month.JULY
import java.util.*

@DataJpaTest
@Import(TestJpaConfig::class)
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-containers")
@Transactional
class ArchivedRepositoryShould {

  @Autowired
  private lateinit var dateTimeProvider: DateTimeProvider

  @Autowired
  private lateinit var entityManager: EntityManager

  @Autowired
  private lateinit var employerRepository: EmployerRepository

  @Autowired
  private lateinit var jobRepository: JobRepository

  @Autowired
  private lateinit var archivedRepository: ArchivedRepository

  private val jobCreationTime = Instant.parse("2024-01-01T00:00:00Z")
  private val jobRegisterArchivedTime = Instant.parse("2025-03-01T01:00:00Z")
  private val jobReRegisterArchivedTime = Instant.parse("2025-03-02T01:00:00Z")

  private val expectedPrisonNumber = Holder.expectedPrisonNumber
  private val amazonForkliftOperatorJob = Holder.amazonForkliftOperatorJob
  private val amazonEmployer = Holder.amazonEmployer
  private val nonExistentJob = Holder.nonExistentJob

  private object Holder {
    val amazonEmployer = Employer(
      id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
      name = "Amazon",
      description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
      sector = "LOGISTICS",
      status = "KEY_PARTNER",
    )

    val amazonForkliftOperatorJob = Job(
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

    val nonExistentEmployer = Employer(
      id = EntityId("b9c925c1-c0d3-460d-8142-f79e7c292fce"),
      name = "Non-Existent Employer",
      description = "Daydreaming Inc.",
      sector = "LOGISTICS",
      status = "KEY_PARTNER",
    )

    val nonExistentJob = Job(
      id = EntityId("035fa5bb-1523-4469-a2a6-c6cf0ac94173"),
      title = "Non-Existent Job",
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
        This is a daydreaming job :)
      """.trimIndent(),
      offenceExclusions = "NONE,DRIVING",
      isRollingOpportunity = false,
      closingDate = LocalDate.of(2024, JULY, 20),
      isOnlyForPrisonLeavers = true,
      startDate = LocalDate.of(2024, JULY, 20),
      howToApply = "",
      supportingDocumentationRequired = "CV,DISCLOSURE_LETTER",
      supportingDocumentationDetails = "",
      employer = nonExistentEmployer,
    )

    val expectedPrisonNumber = "A1234BC"
  }

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
  fun `archive a job for given prisoner`() {
    val job = obtainTheJobJustCreated()

    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { expressionOfInterest ->
      archivedRepository.saveAndFlush(expressionOfInterest)
    }

    val expressionOfInterestFreshCopy =
      archivedRepository.findById(savedJobArchived.id).orElseThrow()
    assertThat(expressionOfInterestFreshCopy).usingRecursiveComparison().isEqualTo(savedJobArchived)
  }

  @Test
  fun `set createdAt attribute, when archiving job`() {
    val job = obtainTheJobJustCreated()

    val expressionOfInterest = makeJobArchived(job, expectedPrisonNumber)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterArchivedTime))
    val savedJobArchived = archivedRepository.saveAndFlush(expressionOfInterest)

    assertThat(savedJobArchived.createdAt).isEqualTo(jobRegisterArchivedTime)
  }

  @Test
  fun `do NOT update job's attribute, when archiving job`() {
    val job = obtainTheJobJustCreated()

    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { expressionOfInterest ->
      archivedRepository.saveAndFlush(expressionOfInterest)
    }

    val jobFreshCopy = jobRepository.findById(savedJobArchived.job.id).orElseThrow()
    assertThat(jobFreshCopy).usingRecursiveComparison().ignoringFields("expressionsOfInterest").isEqualTo(job)
    assertThat(jobFreshCopy.modifiedAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `do NOT archive again, when it exists`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { expressionOfInterest ->
      archivedRepository.saveAndFlush(expressionOfInterest)
    }

    val duplicateArchived = makeJobArchived(
      job = savedJobArchived.job,
      prisonNumber = savedJobArchived.id.prisonNumber,
    )
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobReRegisterArchivedTime))
    val updatedArchived = archivedRepository.saveAndFlush(duplicateArchived).also {
      entityManager.refresh(it)
    }

    assertThat(updatedArchived).usingRecursiveComparison().isEqualTo(savedJobArchived)
    assertThat(updatedArchived.createdAt).isEqualTo(jobRegisterArchivedTime)
  }

  @Test
  fun `throw exception, when archiving a non-existent job`() {
    val jobId = nonExistentJob.id.toString()
    val expressionOfInterest = makeJobArchived(nonExistentJob, expectedPrisonNumber)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterArchivedTime))

    val exception = assertThrows<Exception> {
      archivedRepository.save(expressionOfInterest)
    }

    assertThat(exception).isInstanceOf(DataAccessException::class.java)
    assertThat(exception.message)
      .contains("Unable to find")
      .contains(jobId)
  }

  @Test
  fun `undo archiving a job for given prisoner`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { expressionOfInterest ->
      archivedRepository.saveAndFlush(expressionOfInterest)
    }

    archivedRepository.deleteById(savedJobArchived.id)

    val searchArchived = archivedRepository.findById(savedJobArchived.id)
    assertThat(searchArchived.isEmpty).isTrue()
  }

  @Test
  fun `do NOT update job's attribute, when undo archiving`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { expressionOfInterest ->
      archivedRepository.saveAndFlush(expressionOfInterest)
    }

    archivedRepository.deleteById(savedJobArchived.id)

    val searchJob = jobRepository.findById(job.id).orElseThrow()
    assertThat(searchJob).usingRecursiveComparison().isEqualTo(job)
  }

  private fun obtainTheJobJustCreated(): Job {
    employerRepository.save(amazonEmployer)
    return jobRepository.save(amazonForkliftOperatorJob).also {
      entityManager.flush()
    }
  }

  private fun makeJobArchived(job: Job, prisonNumber: String): Archived =
    Archived(id = ArchivedId(job.id, prisonNumber), job = job)
}
