package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import java.time.Instant

const val MATCHING_CANDIDATE_PATH_SUFFIX = "matching-candidate"

abstract class MatchingCandidateJobDetailsTestCase : JobsTestCase() {

  protected fun assertGetMatchingCandidateJobDetailsIsOK(
    id: String,
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = "$JOBS_ENDPOINT/$id/$MATCHING_CANDIDATE_PATH_SUFFIX"
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetMatchingCandidateJobDetailsIsNotOK(
    jobId: String,
    parameters: String? = null,
    expectedStatus: HttpStatus,
  ) {
    var url = "$JOBS_ENDPOINT/$jobId/$MATCHING_CANDIDATE_PATH_SUFFIX"
    parameters?.let { url = "$url?$it" }
    assertRequestWithoutBody(
      url = url,
      expectedHttpVerb = HttpMethod.GET,
      expectedStatus = expectedStatus,
    )
  }

  protected fun tescoWarehouseHandlerJobDetailsBody(
    id: String,
    createdAt: Instant = jobCreationTime,
    distance: Float? = null,
    expressionOfInterest: Boolean = false,
    archived: Boolean = false,
  ) = newJobDetailsResponse(
    id = id,
    employerName = "Tesco",
    jobTitle = "Warehouse handler",
    closingDate = null,
    startDate = null,
    postcode = "NE157LR",
    sector = "WAREHOUSING",
    salaryFrom = 99f,
    salaryTo = null,
    salaryPeriod = "PER_DAY",
    additionalSalaryInformation = null,
    workPattern = "FLEXIBLE_SHIFTS",
    hoursPerWeek = "FULL_TIME_40_PLUS",
    contractType = "TEMPORARY",
    offenceExclusions = listOf("CASE_BY_CASE", "OTHER"),
    essentialCriteria = "Essential job criteria",
    desirableCriteria = null,
    description = "Job description\r\nDescribe the role and main tasks. Include any benefits and training opportunities.",
    howToApply = "How to applyHow to apply",
    distance = distance,
    expressionOfInterest = expressionOfInterest,
    archived = archived,
    createdAt = createdAt.toString(),
  )

  protected fun amazonForkliftOperatorJobDetailsBody(
    id: String,
    createdAt: Instant = jobCreationTime,
    distance: Float? = null,
    expressionOfInterest: Boolean = false,
    archived: Boolean = false,
  ) = newJobDetailsResponse(
    id = id,
    employerName = "Amazon",
    jobTitle = "Forklift operator",
    closingDate = "2025-02-01",
    startDate = "2025-05-31",
    sector = "RETAIL",
    postcode = "LS12",
    salaryFrom = 11.93f,
    salaryTo = 15.90f,
    salaryPeriod = "PER_HOUR",
    additionalSalaryInformation = null,
    workPattern = "FLEXIBLE_SHIFTS",
    hoursPerWeek = "FULL_TIME",
    contractType = "TEMPORARY",
    offenceExclusions = listOf("NONE", "DRIVING", "OTH"),
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
    howToApply = "",
    distance = distance,
    expressionOfInterest = expressionOfInterest,
    archived = archived,
    createdAt = createdAt.toString(),
  )

  protected fun abcConstructionJobDetailsBody(
    id: String,
    createdAt: Instant = jobCreationTime,
    distance: Float? = null,
    expressionOfInterest: Boolean = false,
    archived: Boolean = false,
  ) = newJobDetailsResponse(
    id = id,
    employerName = "ABC Construction",
    jobTitle = "Apprentice plasterer",
    closingDate = null,
    sector = "CONSTRUCTION",
    postcode = "NE157LR",
    salaryFrom = 99f,
    salaryTo = null,
    salaryPeriod = "PER_DAY",
    additionalSalaryInformation = null,
    workPattern = "FLEXIBLE_SHIFTS",
    hoursPerWeek = "FULL_TIME_40_PLUS",
    contractType = "TEMPORARY",
    offenceExclusions = listOf("CASE_BY_CASE", "OTHER"),
    essentialCriteria = "Essential job criteria",
    desirableCriteria = null,
    description = "Job description\r\nDescribe the role and main tasks. Include any benefits and training opportunities.",
    howToApply = "How to applyHow to apply",
    startDate = null,
    distance = distance,
    expressionOfInterest = expressionOfInterest,
    archived = archived,
    createdAt = createdAt.toString(),
  )

  private fun newJobDetailsResponse(
    id: String,
    employerName: String,
    jobTitle: String,
    closingDate: String?,
    startDate: String?,
    postcode: String,
    distance: Float?,
    sector: String,
    salaryFrom: Float,
    salaryTo: Float?,
    salaryPeriod: String,
    additionalSalaryInformation: String?,
    workPattern: String,
    hoursPerWeek: String,
    contractType: String,
    offenceExclusions: List<String>,
    essentialCriteria: String,
    desirableCriteria: String?,
    description: String,
    howToApply: String,
    expressionOfInterest: Boolean,
    archived: Boolean,
    createdAt: String,
  ): String {
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

    appendOptionalFieldQuoted("closingDate", closingDate)
    appendOptionalFieldQuoted("startDate", startDate)
    appendOptionalField("distance", distance)
    appendOptionalField("salaryTo", salaryTo)
    appendOptionalField("additionalSalaryInformation", additionalSalaryInformation?.asJson())
    appendOptionalField("desirableCriteria", desirableCriteria?.asJson())

    return """
      {
        "id": "$id",
        "employerName": "$employerName",
        "jobTitle": "$jobTitle",
        "postcode": "$postcode",
        "sector": "$sector",
        "salaryFrom": $salaryFrom,
        "salaryPeriod": "$salaryPeriod",
        "workPattern": "$workPattern",
        "hoursPerWeek": "$hoursPerWeek",
        "contractType": "$contractType", 
        "offenceExclusions": ${offenceExclusions.asJson()},
        "essentialCriteria": ${essentialCriteria.asJson()},
        "description": ${description.asJson()},
        "howToApply": ${howToApply.asJson()},
        "expressionOfInterest": $expressionOfInterest,
        "archived": $archived, 
        $optionalFields
        "createdAt": "$createdAt"
      }
    """.trimIndent()
  }
}
