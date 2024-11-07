package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.candidateMatchingListItemResponseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.PostcodeMother.RELEASE_AREA_POSTCODE

@DisplayName("Matching Candidate GET Should")
class MatchingCandidateGetShould : MatchingCandidateTestCase() {

  @Nested
  @DisplayName("Given Job Board has no jobs")
  inner class GivenJobBoardHasNoJobs {
    @Test
    fun `return a default paginated empty matching candidate Jobs list`() {
      assertGetMatchingCandidateJobsIsOK(
        parameters = "prisonNumber=$prisonNumber",
        expectedResponse = expectedResponseListOf(),
      )
    }
  }

  @Nested
  @DisplayName("Given Job Board has jobs")
  inner class GivenJobsBoardHasJobs {
    @BeforeEach
    fun beforeEach() = givenThreeJobsAreCreated()

    @Test
    fun `return a default paginated matching candidate Jobs list`() {
      assertGetMatchingCandidateJobsIsOK(
        parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50",
        expectedResponse = expectedResponseListOf(
          builder().from(abcConstructionApprentice)
            .withDistanceInMiles(22.0f)
            .buildCandidateMatchingListItemResponseBody(),
          builder().from(amazonForkliftOperator)
            .withDistanceInMiles(20.0f)
            .buildCandidateMatchingListItemResponseBody(),
          tescoWarehouseHandler.candidateMatchingListItemResponseBody,
        ),
      )
    }

    @Nested
    @DisplayName("And jobs have been archived for the candidate")
    inner class AndJobsHasBeenArchived {
      @Test
      fun `return Jobs that have not been archived for the candidate`() {
        assertAddArchived(amazonForkliftOperator.id.id, prisonNumber)
        assertAddArchived(amazonForkliftOperator.id.id, anotherPrisonNumber)
        assertAddArchived(tescoWarehouseHandler.id.id, anotherPrisonNumber)

        assertGetMatchingCandidateJobsIsOK(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50",
          expectedResponse = expectedResponseListOf(
            tescoWarehouseHandler.candidateMatchingListItemResponseBody,
            builder().from(abcConstructionApprentice)
              .withDistanceInMiles(22.0f)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }
    }

    @Nested
    @DisplayName("And candidates have expressed interest")
    inner class AndCandidatesExpressed {
      @Test
      fun `return Jobs tagged with candidate's interest`() {
        assertAddExpressionOfInterest(abcConstructionApprentice.id.id, prisonNumber)
        assertAddExpressionOfInterest(abcConstructionApprentice.id.id, anotherPrisonNumber)
        assertAddExpressionOfInterest(tescoWarehouseHandler.id.id, anotherPrisonNumber)

        assertGetMatchingCandidateJobsIsOK(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50",
          expectedResponse = expectedResponseListOf(
            tescoWarehouseHandler.candidateMatchingListItemResponseBody,
            builder().from(amazonForkliftOperator)
              .withDistanceInMiles(20.0f)
              .buildCandidateMatchingListItemResponseBody(),
            builder()
              .from(abcConstructionApprentice)
              .withDistanceInMiles(22.0f)
              .withExpressionOfInterestFrom(prisonNumber)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }
    }

    @Nested
    @DisplayName("And a custom pagination has been set")
    inner class CustomPagination {
      @Test
      fun `return a custom paginated matching candidate Jobs list`() {
        assertGetMatchingCandidateJobsIsOK(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&page=1&size=1",
          expectedResponse = expectedResponseListOf(
            size = 1,
            page = 1,
            totalElements = 3,
            builder().from(amazonForkliftOperator)
              .withDistanceInMiles(20.0f)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }
    }

    @Nested
    @DisplayName("And a custom filter has been set")
    inner class CustomFilter {
      @Test
      fun `return Jobs filtered by job sector`() {
        assertGetMatchingCandidateJobsIsOK(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&sectors=retail",
          expectedResponse = expectedResponseListOf(
            builder().from(amazonForkliftOperator)
              .withDistanceInMiles(20.0f)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }

      @Test
      fun `return Jobs filtered by various job sectors`() {
        assertGetMatchingCandidateJobsIsOK(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&sectors=retail,warehousing",
          expectedResponse = expectedResponseListOf(
            tescoWarehouseHandler.candidateMatchingListItemResponseBody,
            builder().from(amazonForkliftOperator)
              .withDistanceInMiles(20.0f)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }

      @Test
      fun `return Jobs filtered by search radius`() {
        val searchRadiusInMiles = 10

        assertGetMatchingCandidateJobsIsOK(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=$searchRadiusInMiles",
          expectedResponse = expectedResponseListOf(
            tescoWarehouseHandler.candidateMatchingListItemResponseBody,
          ),
        )
      }
    }

    @Nested
    @DisplayName("And a custom sorting order has been set")
    inner class AndOrderHasBeenSet {
      @Test
      fun `return Jobs list sorted by job title, in ascending order`() {
        assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&sortBy=jobTitle&sortOrder=asc",
          expectedJobTitlesSorted = listOf(
            "Apprentice plasterer",
            "Forklift operator",
            "Warehouse handler",
          ),
        )
      }

      @Test
      fun `return Jobs sorted by job title, in descending order`() {
        assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&sortBy=jobTitle&sortOrder=desc",
          expectedJobTitlesSorted = listOf(
            "Warehouse handler",
            "Forklift operator",
            "Apprentice plasterer",
          ),
        )
      }

      @Test
      fun `return Jobs sorted by closing date, in ascending order, by default`() {
        assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&sortBy=closingDate",
          expectedSortingOrder = "asc",
        )
      }

      @Test
      fun `return Jobs sorted by closing date, in ascending order`() {
        assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&sortBy=closingDate&sortOrder=asc",
          expectedSortingOrder = "asc",
        )
      }

      @Test
      fun `return Jobs sorted by closing date, in descending order`() {
        assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
          parameters = "prisonNumber=$prisonNumber&releaseArea=$RELEASE_AREA_POSTCODE&searchRadius=50&sortBy=closingDate&sortOrder=desc",
          expectedSortingOrder = "desc",
        )
      }
    }
  }

  @Nested
  @DisplayName("Given not all mandatory parameters have been provided")
  inner class GivenMissingMandatoryParameters {
    @Test
    fun `return 400 Bad Request error message when prison number is missing`() {
      assertGetMatchingCandidateJobsReturnsBadRequestError(
        expectedResponse = """
        {
            "status": 400,
            "errorCode": null,
            "userMessage": "Missing required parameter: Required request parameter 'prisonNumber' for method parameter type String is not present",
            "developerMessage": "Required request parameter 'prisonNumber' for method parameter type String is not present",
            "moreInfo": null
        }
      """.trimIndent(),
      )
    }
  }
}
