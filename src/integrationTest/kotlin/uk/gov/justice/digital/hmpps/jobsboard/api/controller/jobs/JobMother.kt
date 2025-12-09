package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.asda
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.jobCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.VALID_PRISON_NUMBER
import java.time.Instant
import java.time.LocalDate
import java.util.UUID.randomUUID

object JobMother {
  val jobCreator = "test-client"
  val jobCreationTime: Instant = Instant.parse("2024-01-01T00:00:00Z")

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
    salaryFrom = 1_234_567.12,
    salaryTo = 7_654_321.21,
    salaryPeriod = "PER_DAY",
    additionalSalaryInformation = "Immediate starts available\nFull training provided",
    isPayingAtLeastNationalMinimumWage = true,
    workPattern = "FLEXI_TIME",
    contractType = "PERMANENT",
    hoursPerWeek = "FULL_TIME_40_PLUS",
    baseLocation = "HYBRID",
    essentialCriteria = "Essential job criteria",
    desirableCriteria = null,
    description = "Job description\r\nDescribe the role and main tasks. Include any benefits and training opportunities.",
    offenceExclusions = "CASE_BY_CASE,OTHER",
    offenceExclusionsDetails = null,
    howToApply = "How to applyHow to apply",
    closingDate = LocalDate.parse("2025-07-01"),
    startDate = LocalDate.parse("2025-12-31"),
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = false,
    supportingDocumentationRequired = "DISCLOSURE_LETTER,OTHER",
    supportingDocumentationDetails = null,
    employer = tesco,
  )

  val asdaWarehouseHandler = Job(
    id = EntityId("04295747-e60d-4e51-9716-e721a63bdd09"),
    title = "Warehouse handler",
    sector = "WAREHOUSING",
    industrySector = "LOGISTICS",
    numberOfVacancies = 1,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postcode = "NE207LR",
    salaryFrom = 1_234_567.12,
    salaryTo = 7_654_321.21,
    salaryPeriod = "PER_DAY",
    additionalSalaryInformation = "Immediate starts available\nFull training provided",
    isPayingAtLeastNationalMinimumWage = true,
    workPattern = "FLEXI_TIME",
    contractType = "PERMANENT",
    hoursPerWeek = "FULL_TIME_40_PLUS",
    baseLocation = "HYBRID",
    essentialCriteria = "Essential job criteria",
    desirableCriteria = null,
    description = "Job description\r\nDescribe the role and main tasks. Include any benefits and training opportunities.",
    offenceExclusions = "CASE_BY_CASE,OTHER",
    offenceExclusionsDetails = null,
    howToApply = "How to applyHow to apply",
    closingDate = LocalDate.parse("2025-07-01"),
    startDate = LocalDate.parse("2025-12-31"),
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = false,
    supportingDocumentationRequired = "DISCLOSURE_LETTER,OTHER",
    supportingDocumentationDetails = null,
    employer = asda,
  )

  val amazonForkliftOperator = Job(
    id = EntityId("d3035924-f9fe-426f-b253-f7c8225167ae"),
    title = "Forklift operator",
    sector = "RETAIL",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "Switchback",
    postcode = "LS12",
    salaryFrom = 11.93,
    salaryTo = 15.90,
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
    offenceExclusions = "NONE,DRIVING,OTHER",
    offenceExclusionsDetails = """
      More details of other offence exclusions:
      - drunken at pub
      - war crime
    """.trimIndent(),
    isRollingOpportunity = false,
    closingDate = LocalDate.parse("2025-02-01"),
    isOnlyForPrisonLeavers = true,
    startDate = LocalDate.parse("2025-05-31"),
    howToApply = "",
    supportingDocumentationRequired = "CV,DISCLOSURE_LETTER",
    supportingDocumentationDetails = "",
    employer = amazon,
  )

  val amazonNationalForkliftOperator = Job(
    id = EntityId("d3035924-f9fe-426f-b253-f7c8225167af"),
    title = "National forklift operator",
    sector = "RETAIL",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "Switchback",
    postcode = null,
    isNational = true,
    salaryFrom = 11.93,
    salaryTo = 15.90,
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
    offenceExclusions = "NONE,DRIVING,OTHER",
    offenceExclusionsDetails = """
      More details of other offence exclusions:
      - drunken at pub
      - war crime
    """.trimIndent(),
    isRollingOpportunity = false,
    closingDate = null,
    isOnlyForPrisonLeavers = true,
    startDate = LocalDate.parse("2025-05-31"),
    howToApply = "",
    supportingDocumentationRequired = "CV,DISCLOSURE_LETTER",
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
    postcode = "GU3 3DU",
    salaryFrom = 99.0,
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
    offenceExclusions = "CASE_BY_CASE,OTHER",
    offenceExclusionsDetails = null,
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = null,
    supportingDocumentationDetails = null,
    employer = abcConstruction,
  )

  val abcNationalConstructionApprentice = Job(
    id = EntityId("6fdf2bf4-cfe6-419c-bab2-b3673adbb394"),
    title = "National Apprentice plasterer",
    sector = "CONSTRUCTION",
    industrySector = "CONSTRUCTION",
    numberOfVacancies = 3,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postcode = null,
    isNational = true,
    salaryFrom = 99.0,
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
    offenceExclusions = "CASE_BY_CASE,OTHER",
    offenceExclusionsDetails = null,
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = null,
    supportingDocumentationDetails = null,
    employer = abcConstruction,
  )

  fun builder(): JobBuilder = JobBuilder()

  val Job.requestBody: String get() = jobRequestBody(this)
  val Job.responseBody: String get() = jobResponseBody(this)
  val Job.itemListResponseBody: String get() = jobItemListResponseBody(this)
  val Job.candidateMatchingListItemResponseBody: String get() = matchingCandidateJobItemListResponseBody(this)
  val Job.closingSoonListResponseBody: String get() = closingSoonListResponseBody(this)

  fun Job.candidateMatchingItemListResponseBody(prisonNumber: String, distance: Double): String = matchingCandidateJobItemListResponseBody(this, prisonNumber, distance)

  private fun jobRequestBody(job: Job): String = jobBody(job)

  private fun jobResponseBody(job: Job): String {
    val expectedJob = job.copy().also {
      it.createdAt = jobCreationTime
    }
    return jobBody(expectedJob)
  }

  private fun jobItemListResponseBody(job: Job): String = """
        {
          "employerId": "${job.employer.id.id}",
          "employerName": "${job.employer.name}",
          "jobTitle": "${job.title}",
          "numberOfVacancies": ${job.numberOfVacancies},
          "sector": "${job.sector}",
          "createdAt": "$jobCreationTime",
          "createdBy": "$jobCreator"
        }
  """.trimIndent()

  private fun jobBody(job: Job): String {
    val createdAt = job.createdAt?.let { ",\n\"createdAt\": \"$it\"" } ?: ""
    return """
        {
          "employerId": "${job.employer.id.id}",
          "jobTitle": "${job.title}",
          "sector": "${job.sector}",
          "industrySector": "${job.industrySector}",
          "numberOfVacancies": ${job.numberOfVacancies},
          "sourcePrimary": "${job.sourcePrimary}",
          "sourceSecondary": ${job.sourceSecondary?.asJson()},
          "charityName": ${job.charityName?.asJson()},
          "postCode": ${job.postcode?.asJson()},
          "salaryFrom": ${job.salaryFrom},
          "salaryTo": ${job.salaryTo},
          "salaryPeriod": "${job.salaryPeriod}",
          "additionalSalaryInformation": ${job.additionalSalaryInformation?.asJson()},
          "isPayingAtLeastNationalMinimumWage": ${job.isPayingAtLeastNationalMinimumWage},
          "workPattern": "${job.workPattern}",
          "hoursPerWeek": "${job.hoursPerWeek}",
          "contractType": "${job.contractType}",
          "baseLocation": ${job.baseLocation?.asJson()},
          "essentialCriteria": "${job.essentialCriteria}",
          "desirableCriteria": ${job.desirableCriteria?.asJson()},
          "description": ${job.description.asJson()},
          "offenceExclusions": ${job.offenceExclusions.asStringList()},
          "offenceExclusionsDetails": ${job.offenceExclusionsDetails?.asJson()}, 
          "isRollingOpportunity": ${job.isRollingOpportunity},
          "closingDate": ${job.closingDate?.toString()?.asJson()},
          "isOnlyForPrisonLeavers": ${job.isOnlyForPrisonLeavers},
          "startDate": ${job.startDate?.toString()?.asJson()},
          "howToApply": "${job.howToApply}",
          "supportingDocumentationRequired": ${job.supportingDocumentationRequired?.asStringList()},
          "supportingDocumentationDetails": ${job.supportingDocumentationDetails?.asJson()}$createdAt,
          "isNational": ${job.isNational}
        }
    """.trimIndent()
  }

  private fun matchingCandidateJobItemListResponseBody(
    job: Job,
    prisonNumber: String = VALID_PRISON_NUMBER,
    distance: Double = 1.0,
  ): String = """
        {
          "id": "${job.id}",
          "jobTitle": "${job.title}",
          "employerName": "${job.employer.name}",
          "sector": "${job.sector}",
          "postcode": ${job.postcode?.asJson()},
          "distance": $distance,
          "closingDate": ${job.closingDate?.toString()?.asJson()},
          "hasExpressedInterest": ${job.expressionsOfInterest.containsKey(prisonNumber)},
          "createdAt": "$jobCreationTime"
        }
  """.trimIndent()

  private fun closingSoonListResponseBody(job: Job): String = """
      {
        "id":"${job.id}",
        "employerName":"${job.employer.name}",
        "jobTitle":"${job.title}",
        "closingDate":${job.closingDate?.toString()?.asJson()},
        "sector":"${job.sector}",
        "createdAt":"$jobCreationTime"
      }
  """.trimIndent()
}

class JobBuilder {
  var id: EntityId = EntityId(randomUUID().toString())
  var title: String = "Service Colleague"
  var sector: String = "RETAIL"
  var industrySector: String = "RETAIL"
  var numberOfVacancies: Int = 1
  var sourcePrimary: String = "PEL"
  var sourceSecondary: String? = null
  var charityName: String? = null
  var postcode: String? = "LS11 5AD"
  var isNational: Boolean = false
  var salaryFrom: Double = 96.32
  var salaryTo: Double? = null
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
  var offenceExclusions: String = "CASE_BY_CASE,OTHER"
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
    id = EntityId(randomUUID().toString()),
    name = "ASDA",
    description = "Asda and often styled as ASDA, is a British supermarket and petrol station chain. Its headquarters are in Leeds, England.",
    sector = "RETAIL",
    status = "SILVER",

  )
  var archived: Boolean = false
  var distance: Float? = null
  var createdAt: Instant? = jobCreationTime

  fun withExpressionsOfInterest(expressionsOfInterest: MutableMap<String, ExpressionOfInterest>): JobBuilder {
    this.expressionsOfInterest = expressionsOfInterest
    return this
  }

  fun withExpressionOfInterestFrom(prisonNumber: String): JobBuilder {
    this.expressionsOfInterest.put(
      prisonNumber,
      ExpressionOfInterest(
        JobPrisonerId(jobId = this.id, prisonNumber),
        createdBy = null,
        createdAt = null,
        this.build(),
      ),
    )
    return this
  }

  fun withDistanceInMiles(distance: Float? = null): JobBuilder {
    this.distance = distance
    return this
  }

  fun from(job: Job): JobBuilder {
    this.id = job.id
    this.title = job.title
    this.sector = job.sector
    this.industrySector = job.industrySector
    this.numberOfVacancies = job.numberOfVacancies
    this.sourcePrimary = job.sourcePrimary
    this.sourceSecondary = job.sourceSecondary
    this.charityName = job.charityName
    this.postcode = job.postcode
    this.isNational = job.isNational
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

  fun build(): Job = Job(
    id = this.id,
    title = this.title,
    sector = this.sector,
    industrySector = this.industrySector,
    numberOfVacancies = this.numberOfVacancies,
    sourcePrimary = this.sourcePrimary,
    sourceSecondary = this.sourceSecondary,
    charityName = this.charityName,
    postcode = this.postcode,
    isNational = this.isNational,
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

  fun buildCandidateMatchingListItemResponseBody(): String = """
      {
        "id": "${this.id}",
        "jobTitle": "${this.title}",
        "employerName": "${this.employer.name}",
        "sector": "${this.sector}",
        "postcode": ${this.postcode?.asJson()},
        "distance": ${this.distance},
        "closingDate": ${this.closingDate?.toString()?.asJson()},
        "hasExpressedInterest": ${this.expressionsOfInterest.containsKey(VALID_PRISON_NUMBER)},
        "createdAt": "$jobCreationTime"
      }
  """.trimIndent()

  fun buildJobDetailsResponseBody(prisonNumber: String): String {
    val optionalFields = StringBuilder()
    val appendOptionalFieldWithOrWithoutQuote: (String, Any?, Boolean) -> Unit =
      { name: String, value: Any?, withQuote: Boolean ->
        value?.let {
          val finalValue = if (withQuote) "\"$value\"" else value
          optionalFields.append("\"$name\": $finalValue,\n")
        }
      }
    val appendOptionalField: (String, Any?) -> Unit =
      { name: String, value: Any? -> appendOptionalFieldWithOrWithoutQuote(name, value, false) }
    val appendOptionalFieldQuoted: (String, Any?) -> Unit =
      { name: String, value: Any? -> appendOptionalFieldWithOrWithoutQuote(name, value, true) }

    appendOptionalFieldQuoted("closingDate", this.closingDate)
    appendOptionalFieldQuoted("startDate", this.startDate)
    appendOptionalField("distance", this.distance)
    appendOptionalField("salaryTo", this.salaryTo)
    appendOptionalField("additionalSalaryInformation", this.additionalSalaryInformation?.asJson())
    appendOptionalField("desirableCriteria", this.desirableCriteria?.asJson())
    appendOptionalField("offenceExclusionsDetails", this.offenceExclusionsDetails?.asJson())
    appendOptionalField("charityName", this.charityName?.asJson())

    return """
      {
        "id": "${this.id}",
        "employerName": "${this.employer.name}",
        "jobTitle": "${this.title}",
        "postcode": ${this.postcode?.asJson()},
        "sector": "${this.sector}",
        "salaryFrom": ${this.salaryFrom},
        "salaryPeriod": "${this.salaryPeriod}",
        "workPattern": "${this.workPattern}",
        "hoursPerWeek": "${this.hoursPerWeek}",
        "contractType": "${this.contractType}",
        "numberOfVacancies": ${this.numberOfVacancies},
        "isOnlyForPrisonLeavers": ${this.isOnlyForPrisonLeavers}, 
        "offenceExclusions": ${this.offenceExclusions.asStringList()},
        "essentialCriteria": ${this.essentialCriteria.asJson()},
        "description": ${this.description.asJson()},
        "howToApply": ${this.howToApply.asJson()},
        "expressionOfInterest": ${this.expressionsOfInterest.containsKey(prisonNumber)},
        "archived": ${this.archived}, 
        $optionalFields
        "createdAt": "${this.createdAt}"
      }
    """.trimIndent()
  }
}

private val mapper: ObjectMapper = jacksonObjectMapper()

internal fun String.asJson(): String = mapper.writeValueAsString(this)

internal fun String.asStringList() = asList().asStringList()

internal fun String.asList() = this.split(",").map { it.trim() }.toList()

internal fun List<String>.asStringList() = joinToString(separator = ",", prefix = "[", postfix = "]") { "\"$it\"" }
