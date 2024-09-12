package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test

class MatchingCandidateGetShould : MatchingCandidateTestCase() {
  @Test
  fun `retrieve a default paginated empty matching candidate Jobs list`() {
    assertGetMatchingCandidateJobsIsOK(
      expectedResponse = expectedResponseListOf(),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOK(
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerMatchingCandidateJobItemListResponse(jobCreationTime),
        amazonForkliftOperatorMatchingCandidateJobItemListResponse(jobCreationTime),
        abcConstructionMatchingCandidateJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a custom paginated matching candidate Jobs list`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOK(
      parameters = "page=1&size=1",
      expectedResponse = expectedResponseListOf(
        size = 1,
        page = 1,
        totalElements = 3,
        amazonForkliftOperatorMatchingCandidateJobItemListResponse(jobCreationTime),
      ),
    )
  }
}