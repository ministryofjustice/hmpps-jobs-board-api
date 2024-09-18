package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test

class MatchingCandidateJobDetailsGetShould : MatchingCandidateJobDetailsTestCase() {
  @Test
  fun `retrieve details of a matching candidate job`() {
    assertGetMatchingCandidateJobDetailsIsOK(
      jobId = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
      parameters = null,
      expectedResponse = """
        {
          "id": "89de6c84-3372-4546-bbc1-9d1dc9ceb354"
        }
      """.trimIndent(),
    )
  }
}
