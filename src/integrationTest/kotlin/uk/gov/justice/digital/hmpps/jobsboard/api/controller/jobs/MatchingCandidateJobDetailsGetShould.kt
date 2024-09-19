package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class MatchingCandidateJobDetailsGetShould : MatchingCandidateJobDetailsTestCase() {
  @BeforeEach
  override fun setup() {
    super.setup()
    givenThreeJobsAreCreated()
  }

  @Test
  fun `retrieve details of a matching candidate job`() {
    val jobId = "04295747-e60d-4e51-9716-e721a63bdd06"

    assertGetMatchingCandidateJobDetailsIsOK(
      id = jobId,
      parameters = null,
      expectedResponse = tescoWarehouseHandlerJobDetailsBody(
        id = jobId,
        expressionOfInterest = false,
        archived = false,
      ),
    )
  }

  @Test
  fun `retrieve details of a matching candidate job, with prisonNumber, ExpressionOfInterest and without Archived`() {
    val jobId = "6fdf2bf4-cfe6-419c-bab2-b3673adbb393"

    assertGetMatchingCandidateJobDetailsIsOK(
      id = jobId,
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = abcConstructionJobDetailsBody(
        id = jobId,
        expressionOfInterest = true,
        archived = false,
      ),
    )
  }

  @Test
  fun `retrieve details of a matching candidate job, with prisonNumber, Archived and without ExpressionOfInterest`() {
    val jobId = "d3035924-f9fe-426f-b253-f7c8225167ae"
    assertAddArchived(jobId, prisonNumber)

    assertGetMatchingCandidateJobDetailsIsOK(
      id = jobId,
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = amazonForkliftOperatorJobDetailsBody(
        id = jobId,
        expressionOfInterest = false,
        archived = true,
      ),
    )
  }

  @Test
  fun `receive nothing with non-existent job`() {
    assertGetMatchingCandidateJobDetailsIsNotOK(
      jobId = UUID.randomUUID().toString(),
      expectedStatus = HttpStatus.NOT_FOUND,
    )
  }
}
