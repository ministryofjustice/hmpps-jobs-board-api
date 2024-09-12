package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpStatus.OK
import java.time.Instant

const val MATCHING_CANDIDATE_ENDPOINT = "${JOBS_ENDPOINT}/matching-candidate"

abstract class MatchingCandidateTestCase : JobsTestCase() {

  protected fun assertGetMatchingCandidateJobsIsOK(
    parameters: String? = null,
    expectedResponse: String
  ) {
    var url = MATCHING_CANDIDATE_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun amazonForkliftOperatorMatchingCandidateJobItemListResponse(createdAt: Instant): String = newMatchingCandidateJobItemListResponse(
    jobTitle = "Forklift operator",
    employerName = "Amazon",
    sector = "RETAIL",
    postcode = "LS12",
    distance = 0f,
    closingDate = "2025-02-01",
    expressionOfInterest = false,
    createdAt = createdAt.toString(),
  )

  protected fun tescoWarehouseHandlerMatchingCandidateJobItemListResponse(createdAt: Instant): String = newMatchingCandidateJobItemListResponse(
    jobTitle = "Warehouse handler",
    employerName = "Tesco",
    sector = "WAREHOUSING",
    postcode = "NE157LR",
    distance = 0f,
    closingDate = null,
    expressionOfInterest = false,
    createdAt = createdAt.toString(),
  )

  protected fun abcConstructionMatchingCandidateJobItemListResponse(createdAt: Instant): String = newMatchingCandidateJobItemListResponse(
    jobTitle = "Apprentice plasterer",
    employerName = "ABC Construction",
    sector = "CONSTRUCTION",
    postcode = "NE157LR",
    distance = 0f,
    closingDate = null,
    expressionOfInterest = false,
    createdAt = createdAt.toString(),
  )

  private fun newMatchingCandidateJobItemListResponse(
    jobTitle: String,
    employerName: String,
    sector: String,
    postcode: String,
    distance: Float,
    closingDate: String?,
    expressionOfInterest: Boolean,
    createdAt: String,
  ): String {
    return """
        {
          "jobTitle": "$jobTitle",
          "employerName": "$employerName",
          "sector": "$sector",
          "postcode": "$postcode",
          "distance": $distance,
          "closingDate": ${closingDate?.asJson()},
          "expressionOfInterest": $expressionOfInterest,
          "createdAt": "$createdAt"
        }
    """.trimIndent()
  }
}
