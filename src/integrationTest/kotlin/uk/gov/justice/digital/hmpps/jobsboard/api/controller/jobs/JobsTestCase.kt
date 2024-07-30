package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.ApplicationTestCase
import java.util.UUID.randomUUID

const val JOBS_ENDPOINT = "/jobs"

class JobsTestCase : ApplicationTestCase() {
  protected fun assertAddJobIsCreated(
    body: String,
  ): String {
    return assertAddJob(
      body = body,
      expectedStatus = CREATED,
    )
  }

  protected fun assertAddJobThrowsValidationError(
    jobId: String? = null,
    body: String,
    expectedResponse: String,
  ) {
    assertAddJob(
      jobId = jobId,
      body = body,
      expectedStatus = BAD_REQUEST,
      expectedResponse = expectedResponse,
    )
  }

  private fun assertAddJob(
    jobId: String? = null,
    body: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
  ): String {
    val finalJobId = jobId ?: randomUUID().toString()
    assertRequestWithBody(
      url = "$JOBS_ENDPOINT/$finalJobId",
      body = body,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
    return finalJobId
  }

  protected fun assertGetJobIsOK(
    jobId: String? = null,
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = JOBS_ENDPOINT
    jobId?.let { url = "$url/$it" }
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  // employerId = "0190fd9b-774b-7332-984a-96f4df78774d",

  protected val abcConstructionJobBody: String = newJobBody(
    employerId = "bf392249-b360-4e3e-81a0-8497047987e8",
    jobTitle = "Warehouse operator",
    sector = "WAREHOUSING",
    industrySector = "CONSTRUCTION",
    numberOfVacancies = 1,
    sourcePrimary = "DWP",
    sourceSecondary = "DWP",
    charityName = "dadasdads",
    postCode = "NE157LR",
    salaryFrom = 99f,
    salaryTo = 260f,
    salaryPeriod = "PER_DAY",
    additionalSalaryInformation = "10% Performance bonus",
    isPayingAtLeastNationalMinimumWage = true,
    workPattern = "FLEXIBLE_SHIFTS",
    contractType = "TEMPORARY",
    hoursPerWeek = "FULL_TIME_40_PLUS",
    baseLocation = "HYBRID",
    essentialCriteria = "Essential job criteria",
    desirableCriteria = "Desirable job criteria (optional)",
    description = "Job description\r\nDescribe the role and main tasks. Include any benefits and training opportunities.",
    offenceExclusions = listOf("CASE_BY_CASE", "OTHER"),
    howToApply = "How to applyHow to apply",
    closingDate = "2025-02-01",
    startDate = "2025-02-01",
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = listOf("DISCLOSURE_LETTER", "OTHER"),
    supportingDocumentationDetails = "Some text",
  )

  protected val amazonForkliftOperatorJobBody: String = newJobBody(
    employerId = "bf392249-b360-4e3e-81a0-8497047987e8",
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
    closingDate = "2025-02-01",
    isOnlyForPrisonLeavers = true,
    startDate = "2025-05-31",
    howToApply = "",
    supportingDocumentationRequired = listOf("CV", "DISCLOSURE_LETTER"),
    supportingDocumentationDetails = "",
  )

  private fun newJobBody(
    employerId: String,
    jobTitle: String,
    sector: String,
    industrySector: String,
    numberOfVacancies: Int,
    sourcePrimary: String,
    sourceSecondary: String? = null,
    charityName: String? = null,
    postCode: String,
    salaryFrom: Float,
    salaryTo: Float? = null,
    salaryPeriod: String,
    additionalSalaryInformation: String? = null,
    isPayingAtLeastNationalMinimumWage: Boolean,
    workPattern: String,
    hoursPerWeek: String,
    contractType: String,
    baseLocation: String,
    essentialCriteria: String,
    desirableCriteria: String? = null,
    description: String,
    offenceExclusions: List<String>,
    isRollingOpportunity: Boolean,
    closingDate: String? = null,
    isOnlyForPrisonLeavers: Boolean,
    startDate: String? = null,
    howToApply: String,
    supportingDocumentationRequired: List<String>,
    supportingDocumentationDetails: String? = null,
  ): String {
    return """
        {
          "employerId": "$employerId",
          "jobTitle": "$jobTitle",
          "sector": "$sector",
          "industrySector": "$industrySector",
          "numberOfVacancies": $numberOfVacancies,
          "sourcePrimary": "$sourcePrimary",
          "sourceSecondary": "$sourceSecondary",
          "charityName": "$charityName",
          "postCode": "$postCode",
          "salaryFrom": $salaryFrom,
          "salaryTo": $salaryTo,
          "salaryPeriod": "$salaryPeriod",
          "additionalSalaryInformation": "$additionalSalaryInformation",
          "isPayingAtLeastNationalMinimumWage": $isPayingAtLeastNationalMinimumWage,
          "workPattern": "$workPattern",
          "hoursPerWeek": "$hoursPerWeek",
          "contractType": "$contractType",
          "baseLocation": "$baseLocation",
          "essentialCriteria": "$essentialCriteria",
          "desirableCriteria": "$desirableCriteria",
          "description": ${description.asJson()},
          "offenceExclusions": ${offenceExclusions.asJson()},
          "isRollingOpportunity": $isRollingOpportunity,
          "closingDate": "$closingDate",
          "isOnlyForPrisonLeavers": $isOnlyForPrisonLeavers,
          "startDate": "$startDate",
          "howToApply": "$howToApply",
          "supportingDocumentationRequired": ${supportingDocumentationRequired.asJson()},
          "supportingDocumentationDetails": "$supportingDocumentationDetails"
        }
    """.trimIndent()
  }

  private fun String.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }

  private fun List<String>.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }
}
