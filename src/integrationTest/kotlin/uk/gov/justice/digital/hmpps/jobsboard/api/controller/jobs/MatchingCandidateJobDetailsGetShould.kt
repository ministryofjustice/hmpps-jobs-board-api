package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import java.util.*

class MatchingCandidateJobDetailsGetShould : MatchingCandidateJobDetailsTestCase() {
  @BeforeEach
  override fun setup() {
    super.setup()
    givenThreeJobsAreCreated()
  }

  @Test
  fun `retrieve details of a matching candidate job`() {
    assertGetMatchingCandidateJobDetailsIsOK(
      id = tescoWarehouseHandler.id.id,
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = builder()
        .from(tescoWarehouseHandler)
//        .withDistance(distanceInMiles)
        .buildJobDetailsResponseBody(prisonNumber),
    )
  }

  @Test
  fun `retrieve details of a matching candidate job, with prisonNumber, ExpressionOfInterest and without Archived`() {
    assertAddExpressionOfInterest(abcConstructionApprentice.id.id, prisonNumber)

    assertGetMatchingCandidateJobDetailsIsOK(
      id = abcConstructionApprentice.id.id,
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = builder()
        .from(abcConstructionApprentice)
        .withExpressionOfInterestFrom(prisonNumber)
        .buildJobDetailsResponseBody(prisonNumber),
    )
  }

  @Test
  fun `retrieve details of a matching candidate job, with prisonNumber, Archived and without ExpressionOfInterest`() {
    // TODO: this test is wrong.
    //  If a job is archived we cannot obtain the details.
    //  We must, instead, return a 404 Not Found
    assertAddArchived(amazonForkliftOperator.id.id, prisonNumber)

    assertGetMatchingCandidateJobDetailsIsOK(
      id = abcConstructionApprentice.id.id,
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = builder()
        .from(abcConstructionApprentice)
        .buildJobDetailsResponseBody(prisonNumber),
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
