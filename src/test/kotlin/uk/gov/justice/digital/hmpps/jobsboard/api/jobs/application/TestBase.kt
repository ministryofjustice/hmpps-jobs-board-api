package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import java.time.LocalDate
import java.time.Month.JULY

@ExtendWith(MockitoExtension::class)
abstract class TestBase {

  @Mock
  protected lateinit var jobRepository: JobRepository

  @Mock
  protected lateinit var employerRepository: EmployerRepository

  protected val amazonEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Amazon",
    description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
    sector = "LOGISTICS",
    status = "KEY_PARTNER",
  )

  private val amazonForkliftOperatorJob = Job(
    id = EntityId("fe5d5175-5a21-4cec-a30b-fd87a5f76ce7"),
    title = "Forklift operator",
    sector = "WAREHOUSING",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "Switchback",
    postcode = "LS12",
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
    offenceExclusions = "NONE,DRIVING,OTH",
    offenceExclusionsDetails = """
      More details of other offence exclusions:
      - drunken at pub
      - war crime
    """.trimIndent(),
    isRollingOpportunity = false,
    closingDate = LocalDate.of(2024, JULY, 20),
    isOnlyForPrisonLeavers = true,
    startDate = LocalDate.of(2024, JULY, 20),
    howToApply = "",
    supportingDocumentationRequired = "CV,DISCLOSURE_LETTER",
    supportingDocumentationDetails = "",
    employer = amazonEmployer,
  )

  protected val expectedJob = amazonForkliftOperatorJob

  protected fun deepCopy(job: Job): Job = job.deepCopyMe()

  protected fun Job.deepCopyMe(): Job = this.copy(
    id = this.id.copy(),
    employer = this.employer.copy(),
    expressionsOfInterest = this.expressionsOfInterest.toMutableMap(),
    archived = this.archived.toMutableMap(),
  ).apply {
    expressionsOfInterest.forEach {
      expressionsOfInterest[it.key] = it.value.deepCopy(job = this)
    }
    archived.forEach { pair ->
      archived[pair.key] = pair.value.deepCopy(job = this)
    }
  }

  fun ExpressionOfInterest.deepCopy(job: Job): ExpressionOfInterest {
    val expressionOfInterest = this
    return expressionOfInterest.copy(
      id = expressionOfInterest.id.copy(
        jobId = expressionOfInterest.id.jobId.copy(id = expressionOfInterest.id.jobId.id),
      ),
      job = job,
    )
  }

  fun Archived.deepCopy(job: Job): Archived {
    val archived = this
    return archived.copy(
      id = archived.id.copy(
        jobId = archived.id.jobId.copy(id = archived.id.jobId.id),
      ),
      job = job,
    )
  }
}
