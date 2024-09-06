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
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.config.TestJpaConfig
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
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
class ExpressionOfInterestRepositoryShould {

  @Autowired
  private lateinit var dateTimeProvider: DateTimeProvider

  @Autowired
  private lateinit var entityManager: EntityManager

  @Autowired
  private lateinit var employerRepository: EmployerRepository

  @Autowired
  private lateinit var jobRepository: JobRepository

  @Autowired
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  private val jobCreationTime = Instant.parse("2024-01-01T00:00:00Z")
  private val jobRegisterExpressionOfInterestTime = Instant.parse("2025-03-01T01:00:00Z")
  private val jobReRegisterExpressionOfInterestTime = Instant.parse("2025-03-02T01:00:00Z")

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
  fun `save prisoner's ExpressionOfInterest to an existing job`() {
    val job = obtainTheJobJustCreated()

    val savedExpressionOfInterest = makeExpressionOfInterest(job, expectedPrisonNumber).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    val expressionOfInterestFreshCopy =
      expressionOfInterestRepository.findById(savedExpressionOfInterest.id).orElseThrow()
    assertThat(expressionOfInterestFreshCopy).usingRecursiveComparison().isEqualTo(savedExpressionOfInterest)
  }

  @Test
  fun `set createdAt attribute, when saving a new ExpressionOfInterest`() {
    val job = obtainTheJobJustCreated()

    val expressionOfInterest = makeExpressionOfInterest(job, expectedPrisonNumber)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = expressionOfInterestRepository.saveAndFlush(expressionOfInterest)

    assertThat(savedExpressionOfInterest.createdAt).isEqualTo(jobRegisterExpressionOfInterestTime)
  }

  @Test
  fun `do NOT update job's attribute, when saving a new ExpressionOfInterest`() {
    val job = obtainTheJobJustCreated()

    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, expectedPrisonNumber).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    val jobFreshCopy = jobRepository.findById(savedExpressionOfInterest.job.id).orElseThrow()
    assertThat(jobFreshCopy).usingRecursiveComparison().ignoringFields("expressionsOfInterest").isEqualTo(job)
    assertThat(jobFreshCopy.modifiedAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `do NOT update ExpressionOfInterest, when it exists`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, expectedPrisonNumber).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    val duplicateExpressionOfInterest = makeExpressionOfInterest(
      job = savedExpressionOfInterest.job,
      prisonNumber = savedExpressionOfInterest.id.prisonNumber,
    )
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobReRegisterExpressionOfInterestTime))
    val updatedExpressionOfInterest = expressionOfInterestRepository.saveAndFlush(duplicateExpressionOfInterest).also {
      entityManager.refresh(it)
    }

    assertThat(updatedExpressionOfInterest).usingRecursiveComparison().isEqualTo(savedExpressionOfInterest)
    assertThat(updatedExpressionOfInterest.createdAt).isEqualTo(jobRegisterExpressionOfInterestTime)
  }

  @Test
  fun `throw exception, when saving ExpressionOfInterest with non-existent job`() {
    val jobId = nonExistentJob.id.toString()
    val expressionOfInterest = makeExpressionOfInterest(nonExistentJob, expectedPrisonNumber)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))

    val exception = assertThrows<Exception> {
      expressionOfInterestRepository.save(expressionOfInterest)
    }

    assertThat(exception).isInstanceOf(DataAccessException::class.java)
    assertThat(exception.message)
      .contains("Unable to find")
      .contains(jobId)
  }

  @Test
  fun `delete prisoner's ExpressionOfInterest from an existing job`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, expectedPrisonNumber).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    expressionOfInterestRepository.deleteById(savedExpressionOfInterest.id)

    val searchExpressionOfInterest = expressionOfInterestRepository.findById(savedExpressionOfInterest.id)
    assertThat(searchExpressionOfInterest.isEmpty).isTrue()
  }

  @Test
  fun `do NOT update job's attribute, when deleting existing ExpressionOfInterest`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, expectedPrisonNumber).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    expressionOfInterestRepository.deleteById(savedExpressionOfInterest.id)

    val searchJob = jobRepository.findById(job.id).orElseThrow()
    assertThat(searchJob).usingRecursiveComparison().isEqualTo(job)
  }

  private fun obtainTheJobJustCreated(): Job {
    employerRepository.save(amazonEmployer)
    return jobRepository.save(amazonForkliftOperatorJob).also {
      entityManager.flush()
    }
  }

  private fun makeExpressionOfInterest(job: Job, prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = ExpressionOfInterestId(job.id, prisonNumber), job = job)
}
