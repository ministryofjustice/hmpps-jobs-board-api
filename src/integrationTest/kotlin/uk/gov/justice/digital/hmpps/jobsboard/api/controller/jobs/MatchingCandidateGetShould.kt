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
}
