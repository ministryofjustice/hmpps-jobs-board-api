package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpStatus.OK
import java.time.Instant

const val MATCHING_CANDIDATE_ENDPOINT = "${JOBS_ENDPOINT}/matching-candidate"

abstract class MatchingCandidateTestCase : JobsTestCase() {

  protected fun assertGetMatchingCandidateJobsIsOK(
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = MATCHING_CANDIDATE_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
    parameters: String? = "",
    expectedJobTitlesSorted: List<String>,
  ) {
    var url = MATCHING_CANDIDATE_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedJobTitleSortedList = expectedJobTitlesSorted,
    )
  }

  protected fun assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
    parameters: String? = null,
    expectedSortingOrder: String = "asc",
  ) {
    var url = MATCHING_CANDIDATE_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedClosingDateSortingOrder = expectedSortingOrder,
    )
  }

  protected fun amazonForkliftOperatorMatchingCandidateJobItemListResponse(createdAt: Instant): String = newMatchingCandidateJobItemListResponse(
    id = "d3035924-f9fe-426f-b253-f7c8225167ae",
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
    id = "04295747-e60d-4e51-9716-e721a63bdd06",
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
    id = "6fdf2bf4-cfe6-419c-bab2-b3673adbb393",
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
    id: String,
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
          "id": "$id",
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
