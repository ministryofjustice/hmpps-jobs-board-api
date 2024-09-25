package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobCreationTime
import java.time.LocalDate

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
    offenceExclusions = "[\"NONE\", \"DRIVING\"]",
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
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = "[\"DISCLOSURE_LETTER\", \"OTHER\"]",
    supportingDocumentationDetails = null,
    employer = abcConstruction,
  )

  val Job.requestBody: String get() = jobRequestBody(this)
  val Job.responseBody: String get() = jobResponseBody(this)
  val Job.itemListResponseBody: String get() = jobItemListResponseBody(this)
  val Job.candidateMatchingItemListResponseBody: String get() = matchingCandidateJobItemListResponseBody(this)

  private fun jobRequestBody(job: Job): String {
    return jobBody(job)
  }

  private fun jobResponseBody(job: Job): String {
    return jobBody(job)
  }

  private fun jobItemListResponseBody(job: Job): String {
    return """
        {
          "employerId": "${job.employer.id.id}",
          "employerName": "${job.employer.name}",
          "jobTitle": "${job.title}",
          "numberOfVacancies": ${job.numberOfVacancies},
          "sector": "${job.sector}",
          "createdAt": "$jobCreationTime"
        }
    """.trimIndent()
  }

  private fun jobBody(job: Job): String {
    val createdAtField = job.createdAt?.let { ",\n\"createdAt\": \"$it\"" } ?: ""
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
          "postCode": "${job.postcode}",
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
          "offenceExclusions": ${job.offenceExclusions},
          "isRollingOpportunity": ${job.isRollingOpportunity},
          "closingDate": ${job.closingDate?.toString()?.asJson()},
          "isOnlyForPrisonLeavers": ${job.isOnlyForPrisonLeavers},
          "startDate": ${job.startDate?.toString()?.asJson()},
          "howToApply": "${job.howToApply}",
          "supportingDocumentationRequired": ${job.supportingDocumentationRequired},
          "supportingDocumentationDetails": ${job.supportingDocumentationDetails?.asJson()}$createdAtField
        }
    """.trimIndent()
  }

  private fun matchingCandidateJobItemListResponseBody(job: Job): String {
    return """
        {
          "id": "${job.id}",
          "jobTitle": "${job.title}",
          "employerName": "${job.employer.name}",
          "sector": "${job.sector}",
          "postcode": "${job.postcode}",
          "distance": 0,
          "closingDate": ${job.closingDate?.toString()?.asJson()},
          "expressionOfInterest": false,
          "createdAt": "$jobCreationTime"
        }
    """.trimIndent()
  }

  private fun String.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }
}
