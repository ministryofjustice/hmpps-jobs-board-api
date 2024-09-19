package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.ApplicationTestCase
import java.time.Instant
import java.util.*

const val JOBS_ENDPOINT = "/jobs"

class JobsTestCase : ApplicationTestCase() {
  val jobCreationTime = Instant.parse("2024-01-01T00:00:00Z")
  val prisonNumber = "A1234BC"

  @BeforeEach
  override fun setup() {
    super.setup()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobCreationTime))
  }

  protected fun assertAddJobIsCreated(
    body: String,
    id: String? = null,
  ): String {
    return assertAddJob(
      body = body,
      id = id,
      expectedStatus = CREATED,
    )
  }

  protected fun assertUpdateJobIsOk(
    jobId: String,
    body: String,
  ): String {
    return assertAddJob(
      id = jobId,
      body = body,
      expectedStatus = OK,
    )
  }

  protected fun assertAddJobThrowsValidationError(
    jobId: String? = null,
    body: String,
    expectedResponse: String,
  ) {
    assertAddJob(
      id = jobId,
      body = body,
      expectedStatus = BAD_REQUEST,
      expectedResponse = expectedResponse,
    )
  }

  private fun assertAddJob(
    id: String? = null,
    body: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
  ): String {
    val finalJobId = id ?: randomUUID().toString()
    assertRequestWithBody(
      url = "$JOBS_ENDPOINT/$finalJobId",
      body = body,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
    return finalJobId
  }

  protected fun assertGetJobIsOK(
    jobId: String,
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

  protected fun assertGetJobsIsOK(
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = JOBS_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetJobsIsOKAndSortedByJobTitle(
    parameters: String? = "",
    expectedJobTitlesSorted: List<String>,
  ) {
    assertResponse(
      url = "$JOBS_ENDPOINT?$parameters",
      expectedStatus = OK,
      expectedJobTitleSortedList = expectedJobTitlesSorted,
    )
  }

  protected fun assertGetJobsIsOKAndSortedByDate(
    parameters: String? = null,
    expectedSortingOrder: String = "asc",
  ) {
    assertResponse(
      url = "$JOBS_ENDPOINT?$parameters",
      expectedStatus = OK,
      expectedDateSortingOrder = expectedSortingOrder,
    )
  }

  protected fun givenThreeJobsAreCreated() {
    assertAddEmployer(
      id = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    assertAddEmployer(
      id = "182e9a24-6edb-48a6-a84f-b7061f004a97",
      body = abcConstructionBody,
      expectedStatus = CREATED,
    )

    assertAddJobIsCreated(
      id = "04295747-e60d-4e51-9716-e721a63bdd06",
      body = tescoWarehouseHandlerJobBody,
    )

    assertAddJobIsCreated(
      id = "d3035924-f9fe-426f-b253-f7c8225167ae",
      body = amazonForkliftOperatorJobBody,
    )

    assertAddJobIsCreated(
      id = "6fdf2bf4-cfe6-419c-bab2-b3673adbb393",
      body = abcConstructionJobBody,
    )

    assertAddExpressionOfInterest("6fdf2bf4-cfe6-419c-bab2-b3673adbb393", prisonNumber)
  }

  protected val abcConstructionJobBody: String = newJobBody(
    employerId = "182e9a24-6edb-48a6-a84f-b7061f004a97",
    jobTitle = "Apprentice plasterer",
    sector = "CONSTRUCTION",
    industrySector = "CONSTRUCTION",
    numberOfVacancies = 3,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postCode = "NE157LR",
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
    offenceExclusions = listOf("CASE_BY_CASE", "OTHER"),
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = listOf("DISCLOSURE_LETTER", "OTHER"),
    supportingDocumentationDetails = null,
  )

  protected fun abcConstructionJobResponse(createdAt: Instant): String = newJobResponse(
    employerId = "182e9a24-6edb-48a6-a84f-b7061f004a97",
    jobTitle = "Apprentice plasterer",
    sector = "CONSTRUCTION",
    industrySector = "CONSTRUCTION",
    numberOfVacancies = 3,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postCode = "NE157LR",
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
    offenceExclusions = listOf("CASE_BY_CASE", "OTHER"),
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = listOf("DISCLOSURE_LETTER", "OTHER"),
    supportingDocumentationDetails = null,
    createdAt = createdAt.toString(),
  )

  protected fun abcConstructionJobItemListResponse(createdAt: Instant): String = newJobItemListResponse(
    employerId = "182e9a24-6edb-48a6-a84f-b7061f004a97",
    employerName = "ABC Construction",
    jobTitle = "Apprentice plasterer",
    numberOfVacancies = 3,
    sector = "CONSTRUCTION",
    createdAt = createdAt.toString(),
  )

  protected val tescoWarehouseHandlerJobBody: String = newJobBody(
    employerId = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
    jobTitle = "Warehouse handler",
    sector = "WAREHOUSING",
    industrySector = "LOGISTICS",
    numberOfVacancies = 1,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postCode = "NE157LR",
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
    offenceExclusions = listOf("CASE_BY_CASE", "OTHER"),
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = listOf("DISCLOSURE_LETTER", "OTHER"),
    supportingDocumentationDetails = null,
  )

  protected fun tescoWarehouseHandlerJobResponse(createdAt: Instant): String = newJobResponse(
    employerId = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
    jobTitle = "Warehouse handler",
    sector = "WAREHOUSING",
    industrySector = "LOGISTICS",
    numberOfVacancies = 1,
    sourcePrimary = "DWP",
    sourceSecondary = null,
    charityName = null,
    postCode = "NE157LR",
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
    offenceExclusions = listOf("CASE_BY_CASE", "OTHER"),
    howToApply = "How to applyHow to apply",
    closingDate = null,
    startDate = null,
    isRollingOpportunity = false,
    isOnlyForPrisonLeavers = true,
    supportingDocumentationRequired = listOf("DISCLOSURE_LETTER", "OTHER"),
    supportingDocumentationDetails = null,
    createdAt = createdAt.toString(),
  )

  protected fun tescoWarehouseHandlerJobItemListResponse(createdAt: Instant): String = newJobItemListResponse(
    employerId = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
    employerName = "Tesco",
    jobTitle = "Warehouse handler",
    numberOfVacancies = 1,
    sector = "WAREHOUSING",
    createdAt = createdAt.toString(),
  )

  protected val amazonForkliftOperatorJobBody: String = newJobBody(
    employerId = "bf392249-b360-4e3e-81a0-8497047987e8",
    jobTitle = "Forklift operator",
    sector = "RETAIL",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "",
    postCode = "LS12",
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
    offenceExclusions = listOf("NONE", "DRIVING"),
    isRollingOpportunity = false,
    closingDate = "2025-02-01",
    isOnlyForPrisonLeavers = true,
    startDate = "2025-05-31",
    howToApply = "",
    supportingDocumentationRequired = listOf("CV", "DISCLOSURE_LETTER"),
    supportingDocumentationDetails = "",
  )

  protected fun amazonForkliftOperatorJobResponse(createdAt: Instant): String = newJobResponse(
    employerId = "bf392249-b360-4e3e-81a0-8497047987e8",
    jobTitle = "Forklift operator",
    sector = "RETAIL",
    industrySector = "LOGISTICS",
    numberOfVacancies = 2,
    sourcePrimary = "PEL",
    sourceSecondary = "",
    charityName = "",
    postCode = "LS12",
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
    offenceExclusions = listOf("NONE", "DRIVING"),
    isRollingOpportunity = false,
    closingDate = "2025-02-01",
    isOnlyForPrisonLeavers = true,
    startDate = "2025-05-31",
    howToApply = "",
    supportingDocumentationRequired = listOf("CV", "DISCLOSURE_LETTER"),
    supportingDocumentationDetails = "",
    createdAt = createdAt.toString(),
  )

  protected fun amazonForkliftOperatorJobItemListResponse(createdAt: Instant): String = newJobItemListResponse(
    employerId = "bf392249-b360-4e3e-81a0-8497047987e8",
    employerName = "Amazon",
    jobTitle = "Forklift operator",
    numberOfVacancies = 2,
    sector = "RETAIL",
    createdAt = createdAt.toString(),
  )

  private fun newJobResponse(
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
    baseLocation: String? = null,
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
    createdAt: String,
  ): String {
    return """
        {
          "employerId": "$employerId",
          "jobTitle": "$jobTitle",
          "sector": "$sector",
          "industrySector": "$industrySector",
          "numberOfVacancies": $numberOfVacancies,
          "sourcePrimary": "$sourcePrimary",
          "sourceSecondary": ${sourceSecondary?.asJson()},
          "charityName": ${charityName?.asJson()},
          "postCode": "$postCode",
          "salaryFrom": $salaryFrom,
          "salaryTo": $salaryTo,
          "salaryPeriod": "$salaryPeriod",
          "additionalSalaryInformation": ${additionalSalaryInformation?.asJson()},
          "isPayingAtLeastNationalMinimumWage": $isPayingAtLeastNationalMinimumWage,
          "workPattern": "$workPattern",
          "hoursPerWeek": "$hoursPerWeek",
          "contractType": "$contractType",
          "baseLocation": ${baseLocation?.asJson()},
          "essentialCriteria": "$essentialCriteria",
          "desirableCriteria": ${desirableCriteria?.asJson()},
          "description": ${description.asJson()},
          "offenceExclusions": ${offenceExclusions.asJson()},
          "isRollingOpportunity": $isRollingOpportunity,
          "closingDate": ${closingDate?.asJson()},
          "isOnlyForPrisonLeavers": $isOnlyForPrisonLeavers,
          "startDate": ${startDate?.asJson()},
          "howToApply": "$howToApply",
          "supportingDocumentationRequired": ${supportingDocumentationRequired.asJson()},
          "supportingDocumentationDetails": ${supportingDocumentationDetails?.asJson()},
          "createdAt": "$createdAt"
        }
    """.trimIndent()
  }

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
    baseLocation: String? = null,
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
          "sourceSecondary": ${sourceSecondary?.asJson()},
          "charityName": ${charityName?.asJson()},
          "postCode": "$postCode",
          "salaryFrom": $salaryFrom,
          "salaryTo": $salaryTo,
          "salaryPeriod": "$salaryPeriod",
          "additionalSalaryInformation": ${additionalSalaryInformation?.asJson()},
          "isPayingAtLeastNationalMinimumWage": $isPayingAtLeastNationalMinimumWage,
          "workPattern": "$workPattern",
          "hoursPerWeek": "$hoursPerWeek",
          "contractType": "$contractType",
          "baseLocation": ${baseLocation?.asJson()},
          "essentialCriteria": "$essentialCriteria",
          "desirableCriteria": ${desirableCriteria?.asJson()},
          "description": ${description.asJson()},
          "offenceExclusions": ${offenceExclusions.asJson()},
          "isRollingOpportunity": $isRollingOpportunity,
          "closingDate": ${closingDate?.asJson()},
          "isOnlyForPrisonLeavers": $isOnlyForPrisonLeavers,
          "startDate": ${startDate?.asJson()},
          "howToApply": "$howToApply",
          "supportingDocumentationRequired": ${supportingDocumentationRequired.asJson()},
          "supportingDocumentationDetails": ${supportingDocumentationDetails?.asJson()}
        }
    """.trimIndent()
  }

  private fun newJobItemListResponse(
    employerId: String,
    employerName: String,
    jobTitle: String,
    numberOfVacancies: Int,
    sector: String,
    createdAt: String,
  ): String {
    return """
        {
          "employerId": "$employerId",
          "employerName": "$employerName",
          "jobTitle": "$jobTitle",
          "numberOfVacancies": $numberOfVacancies,
          "sector": "$sector",
          "createdAt": "$createdAt"
        }
    """.trimIndent()
  }

  protected fun String.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }

  protected fun List<String>.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }
}
