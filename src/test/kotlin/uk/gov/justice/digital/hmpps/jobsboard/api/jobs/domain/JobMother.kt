package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.CreateJobRequest
import java.time.LocalDate
import java.util.*

object JobMother {
  val tescoWarehouseHandler = Job(
    id = EntityId("04295747-e60d-4e51-9716-e721a63bdd06"),
    title = "Warehouse handler",
    sector = "WAREHOUSING",
    industrySector = "LOGISTICS",
    numberOfVacancies = 1,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postcode = "NE157LR",
    salaryFrom = 99f,
    salaryTo = null,
    salaryPeriod = "PER_DAY",
    additionalSalaryInformation = null,
    isPayingAtLeastNationalMinimumWage = true,
    workPattern = "FLEXIBLE_SHIFTS",
    contractType = "TEMPORARY",
    hoursPerWeek = "FULL_TIME_40_PLUS",
    baseLocation = "HYBRID",
    essentialCriteria = "Essential job criteria",
    desirableCriteria = null,
    description = "Job description\r\nDescribe the role and main tasks. Include any benefits and training opportunities.",
    offenceExclusions = "[\"CASE_BY_CASE\", \"OTHER\"]",
    offenceExclusionsDetails = null,
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = "[\"DISCLOSURE_LETTER\", \"OTHER\"]",
    supportingDocumentationDetails = null,
    employer = tesco,
  )

  val amazonForkliftOperator = Job(
    id = EntityId("d3035924-f9fe-426f-b253-f7c8225167ae"),
    title = "Forklift operator",
    sector = "RETAIL",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "",
    postcode = "LS12",
    salaryFrom = 11.93f,
    salaryTo = 15.90f,
    salaryPeriod = "PER_HOUR",
    additionalSalaryInformation = null,
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
    offenceExclusions = "[\"NONE\", \"DRIVING\", \"OTH\"]",
    offenceExclusionsDetails = "Other offence A, another offence B, yet another offence C",
    isRollingOpportunity = false,
    closingDate = LocalDate.parse("2025-02-01"),
    isOnlyForPrisonLeavers = true,
    startDate = LocalDate.parse("2025-05-31"),
    howToApply = "",
    supportingDocumentationRequired = "[\"CV\", \"DISCLOSURE_LETTER\"]",
    supportingDocumentationDetails = "",
    employer = amazon,
  )

  val abcConstructionApprentice = Job(
    id = EntityId("6fdf2bf4-cfe6-419c-bab2-b3673adbb393"),
    title = "Apprentice plasterer",
    sector = "CONSTRUCTION",
    industrySector = "CONSTRUCTION",
    numberOfVacancies = 3,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postcode = "NE157LR",
    salaryFrom = 99f,
    salaryTo = null,
    salaryPeriod = "PER_DAY",
    additionalSalaryInformation = null,
    isPayingAtLeastNationalMinimumWage = true,
    workPattern = "FLEXIBLE_SHIFTS",
    contractType = "TEMPORARY",
    hoursPerWeek = "FULL_TIME_40_PLUS",
    baseLocation = null,
    essentialCriteria = "Essential job criteria",
    desirableCriteria = null,
    description = "Job description\r\nDescribe the role and main tasks. Include any benefits and training opportunities.",
    offenceExclusions = "[\"CASE_BY_CASE\", \"OTHER\"]",
    offenceExclusionsDetails = null,
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = "[\"DISCLOSURE_LETTER\", \"OTHER\"]",
    supportingDocumentationDetails = null,
    employer = abcConstruction,
  )

  fun builder(): JobBuilder {
    return JobBuilder()
  }

  val Job.createJobRequest: CreateJobRequest get() = createJobRequest(this)

  fun deepCopy(job: Job): Job = job.deepCopyMe()

  fun Job.deepCopyMe(): Job = this.copy(
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

  private fun ExpressionOfInterest.deepCopy(job: Job): ExpressionOfInterest {
    val expressionOfInterest = this
    return expressionOfInterest.copy(
      id = expressionOfInterest.id.copy(
        jobId = expressionOfInterest.id.jobId.copy(id = expressionOfInterest.id.jobId.id),
      ),
      job = job,
    )
  }

  private fun Archived.deepCopy(job: Job): Archived {
    val archived = this
    return archived.copy(
      id = archived.id.copy(
        jobId = archived.id.jobId.copy(id = archived.id.jobId.id),
      ),
      job = job,
    )
  }

  private fun createJobRequest(job: Job): CreateJobRequest {
    return CreateJobRequest.from(
      id = job.id.id,
      employerId = job.employer.id.id,
      jobTitle = job.title,
      sector = job.sector,
      industrySector = job.industrySector,
      numberOfVacancies = job.numberOfVacancies,
      sourcePrimary = job.sourcePrimary,
      sourceSecondary = job.sourceSecondary,
      charityName = job.charityName,
      postCode = job.postcode,
      salaryFrom = job.salaryFrom,
      salaryTo = job.salaryTo,
      salaryPeriod = job.salaryPeriod,
      additionalSalaryInformation = job.additionalSalaryInformation,
      isPayingAtLeastNationalMinimumWage = job.isPayingAtLeastNationalMinimumWage,
      workPattern = job.workPattern,
      hoursPerWeek = job.hoursPerWeek,
      contractType = job.contractType,
      baseLocation = job.baseLocation,
      essentialCriteria = job.essentialCriteria,
      desirableCriteria = job.desirableCriteria,
      description = job.description,
      offenceExclusions = job.offenceExclusions.split(','),
      offenceExclusionsDetails = job.offenceExclusionsDetails,
      isRollingOpportunity = job.isRollingOpportunity,
      closingDate = job.closingDate.toString(),
      isOnlyForPrisonLeavers = job.isOnlyForPrisonLeavers,
      startDate = job.startDate.toString(),
      howToApply = job.howToApply,
      supportingDocumentationRequired = job.supportingDocumentationRequired?.split(','),
      supportingDocumentationDetails = job.supportingDocumentationDetails,
    )
  }
}

class JobBuilder {
  var id: String = UUID.randomUUID().toString()
  var title: String = "Service Colleague"
  var sector: String = "RETAIL"
  var industrySector: String = "RETAIL"
  var numberOfVacancies: Int = 1
  var sourcePrimary: String = "PEL"
  var sourceSecondary: String? = null
  var charityName: String? = null
  var postcode: String = "LS11 5AD"
  var salaryFrom: Float = 96.32f
  var salaryTo: Float? = null
  var salaryPeriod: String = "PER_DAY"
  var additionalSalaryInformation: String? = null
  var isPayingAtLeastNationalMinimumWage: Boolean = true
  var workPattern: String = "FLEXIBLE_SHIFTS"
  var hoursPerWeek: String = "FULL_TIME"
  var contractType: String = "PERMANENT"
  var baseLocation: String? = null
  var essentialCriteria: String = "Essential job criteria"
  var desirableCriteria: String? = null
  var description: String = ""
  var offenceExclusions: String = "[\"CASE_BY_CASE\", \"OTHER\"]"
  var offenceExclusionsDetails: String? = null
  var isRollingOpportunity: Boolean = false
  var closingDate: LocalDate? = null
  var isOnlyForPrisonLeavers: Boolean = true
  var startDate: LocalDate? = null
  var howToApply: String = "How to apply How to apply"
  var supportingDocumentationRequired: String? = null
  var supportingDocumentationDetails: String? = null
  var expressionsOfInterest: MutableMap<String, ExpressionOfInterest> = mutableMapOf()
  var employer: Employer = Employer(
    id = EntityId(UUID.randomUUID().toString()),
    name = "ASDA",
    description = "Asda and often styled as ASDA, is a British supermarket and petrol station chain. Its headquarters are in Leeds, England.",
    sector = "RETAIL",
    status = "SILVER",
  )

  fun withId(id: String): JobBuilder {
    this.id = id
    return this
  }

  fun withExpressionsOfInterest(expressionsOfInterest: MutableMap<String, ExpressionOfInterest>): JobBuilder {
    this.expressionsOfInterest = expressionsOfInterest
    return this
  }

  fun withExpressionOfInterestFrom(prisonNumber: String): JobBuilder {
    this.expressionsOfInterest.put(
      prisonNumber,
      ExpressionOfInterest(
        JobPrisonerId(jobId = EntityId(this.id), prisonNumber),
        createdBy = null,
        createdAt = null,
        this.build(),
      ),
    )
    return this
  }

  fun from(job: Job): JobBuilder {
    this.id = job.id.id
    this.title = job.title
    this.sector = job.sector
    this.industrySector = job.industrySector
    this.numberOfVacancies = job.numberOfVacancies
    this.sourcePrimary = job.sourcePrimary
    this.sourceSecondary = job.sourceSecondary
    this.charityName = job.charityName
    this.postcode = job.postcode
    this.salaryFrom = job.salaryFrom
    this.salaryTo = job.salaryTo
    this.salaryPeriod = job.salaryPeriod
    this.additionalSalaryInformation = job.additionalSalaryInformation
    this.isPayingAtLeastNationalMinimumWage = job.isPayingAtLeastNationalMinimumWage
    this.workPattern = job.workPattern
    this.hoursPerWeek = job.hoursPerWeek
    this.contractType = job.contractType
    this.baseLocation = job.baseLocation
    this.essentialCriteria = job.essentialCriteria
    this.desirableCriteria = job.desirableCriteria
    this.description = job.description
    this.offenceExclusions = job.offenceExclusions
    this.offenceExclusionsDetails = job.offenceExclusionsDetails
    this.isRollingOpportunity = job.isRollingOpportunity
    this.closingDate = job.closingDate
    this.isOnlyForPrisonLeavers = job.isOnlyForPrisonLeavers
    this.startDate = job.startDate
    this.howToApply = job.howToApply
    this.supportingDocumentationRequired = job.supportingDocumentationRequired
    this.supportingDocumentationDetails = job.supportingDocumentationDetails
    this.expressionsOfInterest = job.expressionsOfInterest.mapValues { entry ->
      ExpressionOfInterest(
        id = JobPrisonerId(entry.value.id.jobId, entry.value.id.prisonNumber),
        job = this.build(),
      )
    }.toMutableMap()
    this.employer = Employer(
      id = job.employer.id,
      name = job.employer.name,
      description = job.employer.description,
      sector = job.employer.sector,
      status = job.employer.status,
    )
    return this
  }

  fun build(): Job {
    return Job(
      id = EntityId(this.id),
      title = this.title,
      sector = this.sector,
      industrySector = this.industrySector,
      numberOfVacancies = this.numberOfVacancies,
      sourcePrimary = this.sourcePrimary,
      sourceSecondary = this.sourceSecondary,
      charityName = this.charityName,
      postcode = this.postcode,
      salaryFrom = this.salaryFrom,
      salaryTo = this.salaryTo,
      salaryPeriod = this.salaryPeriod,
      additionalSalaryInformation = this.additionalSalaryInformation,
      isPayingAtLeastNationalMinimumWage = this.isPayingAtLeastNationalMinimumWage,
      workPattern = this.workPattern,
      hoursPerWeek = this.hoursPerWeek,
      contractType = this.contractType,
      baseLocation = this.baseLocation,
      essentialCriteria = this.essentialCriteria,
      desirableCriteria = this.desirableCriteria,
      description = this.description,
      offenceExclusions = this.offenceExclusions,
      offenceExclusionsDetails = this.offenceExclusionsDetails,
      isRollingOpportunity = this.isRollingOpportunity,
      closingDate = this.closingDate,
      isOnlyForPrisonLeavers = this.isOnlyForPrisonLeavers,
      startDate = this.startDate,
      howToApply = this.howToApply,
      supportingDocumentationRequired = this.supportingDocumentationRequired,
      supportingDocumentationDetails = this.supportingDocumentationDetails,
      expressionsOfInterest = this.expressionsOfInterest,
      employer = this.employer,
    )
  }

  fun buildCreateJobRequest(): CreateJobRequest {
    return CreateJobRequest.from(
      id = this.id,
      employerId = this.employer.id.id,
      jobTitle = this.title,
      sector = this.sector,
      industrySector = this.industrySector,
      numberOfVacancies = this.numberOfVacancies,
      sourcePrimary = this.sourcePrimary,
      sourceSecondary = this.sourceSecondary,
      charityName = this.charityName,
      postCode = this.postcode,
      salaryFrom = this.salaryFrom,
      salaryTo = this.salaryTo,
      salaryPeriod = this.salaryPeriod,
      additionalSalaryInformation = this.additionalSalaryInformation,
      isPayingAtLeastNationalMinimumWage = this.isPayingAtLeastNationalMinimumWage,
      workPattern = this.workPattern,
      hoursPerWeek = this.hoursPerWeek,
      contractType = this.contractType,
      baseLocation = this.baseLocation,
      essentialCriteria = this.essentialCriteria,
      desirableCriteria = this.desirableCriteria,
      description = this.description,
      offenceExclusions = this.offenceExclusions.split(','),
      offenceExclusionsDetails = this.offenceExclusionsDetails,
      isRollingOpportunity = this.isRollingOpportunity,
      closingDate = this.closingDate.toString(),
      isOnlyForPrisonLeavers = this.isOnlyForPrisonLeavers,
      startDate = this.startDate.toString(),
      howToApply = this.howToApply,
      supportingDocumentationRequired = this.supportingDocumentationRequired?.split(','),
      supportingDocumentationDetails = this.supportingDocumentationDetails,
    )
  }
}
