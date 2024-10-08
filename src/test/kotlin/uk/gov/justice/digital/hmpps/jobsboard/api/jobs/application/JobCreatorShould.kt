package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.time.LocalDate
import java.time.Month.JULY
import java.util.*

@ExtendWith(MockitoExtension::class)
class JobCreatorShould : TestBase() {

  @InjectMocks
  private lateinit var jobCreator: JobCreator

  private val amazonForkliftOperatorJobRequest = CreateJobRequest.from(
    id = "fe5d5175-5a21-4cec-a30b-fd87a5f76ce7",
    employerId = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
    jobTitle = "Forklift operator",
    sector = "WAREHOUSING",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "Switchback",
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
    offenceExclusions = listOf("NONE", "DRIVING", "OTH"),
    offenceExclusionsDetails = """
      More details of other offence exclusions:
      - drunken at pub
      - war crime
    """.trimIndent(),
    isRollingOpportunity = false,
    closingDate = LocalDate.of(2024, JULY, 20).toString(),
    isOnlyForPrisonLeavers = true,
    startDate = LocalDate.of(2024, JULY, 20).toString(),
    howToApply = "",
    supportingDocumentationRequired = listOf("CV", "DISCLOSURE_LETTER"),
    supportingDocumentationDetails = "",
  )

  @Test
  fun `return true when Job exists`() {
    val jobId = EntityId(UUID.randomUUID().toString())
    whenever(jobRepository.existsById(jobId)).thenReturn(true)

    assertThat(jobCreator.existsById(jobId.id)).isTrue()
  }

  @Test
  fun `return false when Job does not exist`() {
    val jobId = EntityId(UUID.randomUUID().toString())
    whenever(jobRepository.existsById(jobId)).thenReturn(false)

    assertThat(jobCreator.existsById(jobId.id)).isFalse()
  }

  @Test
  fun `save a Job with valid details`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c")))
      .thenReturn(Optional.of(amazonEmployer))

    jobCreator.createOrUpdate(amazonForkliftOperatorJobRequest)

    val jobCaptor = argumentCaptor<Job>()
    verify(jobRepository).save(jobCaptor.capture())
    val actualJob = jobCaptor.firstValue

    assertThat(actualJob).usingRecursiveComparison().isEqualTo(expectedJob)
  }

  @Test
  fun `throw an exception when saving a Job with an invalid UUID`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c")))
      .thenReturn(Optional.of(amazonEmployer))

    val createJobRequest = CreateJobRequest.from(
      id = "invalid-uuid",
      employerId = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
      jobTitle = "Forklift operator",
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
      offenceExclusions = listOf("NONE", "DRIVING"),
      isRollingOpportunity = false,
      closingDate = LocalDate.of(2024, JULY, 20).toString(),
      isOnlyForPrisonLeavers = true,
      startDate = LocalDate.of(2024, JULY, 20).toString(),
      howToApply = "",
      supportingDocumentationRequired = listOf("CV", "DISCLOSURE_LETTER"),
      supportingDocumentationDetails = "",
    )

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.createOrUpdate(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("Invalid UUID format: {${createJobRequest.id}}")
  }

  @Test
  fun `throw exception when saving a Job with an empty UUID`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c")))
      .thenReturn(Optional.of(amazonEmployer))

    val createJobRequest = CreateJobRequest.from(
      id = "",
      employerId = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
      jobTitle = "Forklift operator",
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
      offenceExclusions = listOf("NONE", "DRIVING"),
      isRollingOpportunity = false,
      closingDate = LocalDate.of(2024, JULY, 20).toString(),
      isOnlyForPrisonLeavers = true,
      startDate = LocalDate.of(2024, JULY, 20).toString(),
      howToApply = "",
      supportingDocumentationRequired = listOf("CV", "DISCLOSURE_LETTER"),
      supportingDocumentationDetails = "",
    )

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.createOrUpdate(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be empty")
  }

  @Test
  fun `throw an exception when saving a Job with a null UUID`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c")))
      .thenReturn(Optional.of(amazonEmployer))

    val createJobRequest = CreateJobRequest.from(
      id = "00000000-0000-0000-0000-00000",
      employerId = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
      jobTitle = "Forklift operator",
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
      offenceExclusions = listOf("NONE", "DRIVING"),
      isRollingOpportunity = false,
      closingDate = LocalDate.of(2024, JULY, 20).toString(),
      isOnlyForPrisonLeavers = true,
      startDate = LocalDate.of(2024, JULY, 20).toString(),
      howToApply = "",
      supportingDocumentationRequired = listOf("CV", "DISCLOSURE_LETTER"),
      supportingDocumentationDetails = "",
    )

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.createOrUpdate(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be null: {${createJobRequest.id}}")
  }
}
