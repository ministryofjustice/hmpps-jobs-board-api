package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.Test

class ApplicationHistoriesGetShould : ApplicationHistoriesTestCase() {

  @Test
  fun `return an error, when missing prisonNumber and jobId`() {
    assertGetApplicationHistoriesReturnsBadRequestError()
  }

  @Test
  fun `return an error, when missing jobId`() {
    assertGetApplicationHistoriesReturnsBadRequestError("prisonNumber=A1234BC")
  }

  @Test
  fun `return an error, when missing prisonNumber`() {
    assertGetApplicationHistoriesReturnsBadRequestError("jobId=34ae887f-ceee-444c-8101-1e9bccc3c773")
  }

  @Test
  fun `return an empty list, when there is no job application`() {
    assertGetApplicationHistoriesIsOk(
      parameters = "prisonNumber=A1234BC&jobId=34ae887f-ceee-444c-8101-1e9bccc3c773",
      expectedResponse = "[]",
    )
  }
}
