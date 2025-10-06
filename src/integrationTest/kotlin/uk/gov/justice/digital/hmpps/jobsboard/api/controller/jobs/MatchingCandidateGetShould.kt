package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcNationalConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonNationalForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.candidateMatchingListItemResponseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.PostcodeMother.NO_FIXED_ABODE_POSTCODE
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.PostcodeMother.RELEASE_AREA_POSTCODE

@DisplayName("Matching Candidate GET Should")
class MatchingCandidateGetShould : MatchingCandidateTestCase() {
  private val requestParams = StringBuilder()

  @BeforeEach
  fun setUp() {
    requestParams.clear()
    requestParams.append("prisonNumber=$prisonNumber")
  }

  @Nested
  @DisplayName("Given Job Board has no jobs")
  inner class GivenJobBoardHasNoJobs {
    @Test
    fun `return a default paginated empty matching candidate Jobs list`() {
      assertGetMatchingCandidateJobsIsOK(
        parameters = requestParams.toString(),
        expectedResponse = expectedResponseListOf(),
      )
    }
  }

  @Nested
  @DisplayName("Given Job Board has jobs")
  inner class GivenJobsBoardHasJobs {
    @BeforeEach
    fun beforeEach() = givenRegionalAndNationalJobsAreCreated()

    @Test
    fun `return Jobs list without calculating distance`() {
      assertGetMatchingCandidateJobsIsOK(
        parameters = requestParams.toString(),
        expectedResponse = expectedResponseListOf(
          builder().from(abcConstructionApprentice)
            .withDistanceInMiles(null)
            .buildCandidateMatchingListItemResponseBody(),
          builder().from(amazonForkliftOperator)
            .withDistanceInMiles(null)
            .buildCandidateMatchingListItemResponseBody(),
          builder().from(tescoWarehouseHandler)
            .withDistanceInMiles(null)
            .buildCandidateMatchingListItemResponseBody(),
        ),
      )
    }

    @Test
    fun `return National Jobs list`() {
      requestParams.append("&isNationalJob=true")
      assertGetMatchingCandidateJobsIsOK(
        parameters = requestParams.toString(),
        expectedResponse = expectedResponseListOf(
          builder().from(abcNationalConstructionApprentice)
            .withDistanceInMiles(null)
            .buildCandidateMatchingListItemResponseBody(),
          builder().from(amazonNationalForkliftOperator)
            .withDistanceInMiles(null)
            .buildCandidateMatchingListItemResponseBody(),
        ),
      )
    }

    @Nested
    @DisplayName("And an employer id has been provided")
    inner class AndAndEmployerIdHasBeenProvided {
      @BeforeEach
      fun setUp() {
        requestParams.append("&employerId=${amazonNationalForkliftOperator.employer.id.id}")
      }

      @Test
      fun `return National Jobs list`() {
        requestParams.append("&isNationalJob=true")
        assertGetMatchingCandidateJobsIsOK(
          parameters = requestParams.toString(),
          expectedResponse = expectedResponseListOf(
            builder().from(amazonNationalForkliftOperator)
              .withDistanceInMiles(null)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }

      @Test
      fun `return Jobs list`() {
        assertGetMatchingCandidateJobsIsOK(
          parameters = requestParams.toString(),
          expectedResponse = expectedResponseListOf(
            builder().from(amazonForkliftOperator)
              .withDistanceInMiles(null)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }
    }

    @Nested
    @DisplayName("And a 'no fixed abode' release area postcode has been provided")
    inner class AndNoFixedAbodeReleaseAreaPostcodeHasBeenProvided {
      @BeforeEach
      fun setUp() {
        requestParams.append("&releaseArea=$NO_FIXED_ABODE_POSTCODE")
      }

      @Test
      fun `return Jobs list without calculating distance`() {
        assertGetMatchingCandidateJobsIsOK(
          parameters = requestParams.toString(),
          expectedResponse = expectedResponseListOf(
            builder().from(abcConstructionApprentice)
              .withDistanceInMiles(null)
              .buildCandidateMatchingListItemResponseBody(),
            builder().from(amazonForkliftOperator)
              .withDistanceInMiles(null)
              .buildCandidateMatchingListItemResponseBody(),
            builder().from(tescoWarehouseHandler)
              .withDistanceInMiles(null)
              .buildCandidateMatchingListItemResponseBody(),
          ),
        )
      }

      @Nested
      @DisplayName("And a search radius has been provided")
      inner class AndSearchRadiusHasBeenProvided {
        @BeforeEach
        fun setUp() {
          val searchRadiusInMiles = 20
          requestParams.append("&searchRadius=$searchRadiusInMiles")
        }

        @Test
        fun `return Jobs list without calculating distance`() {
          assertGetMatchingCandidateJobsIsOK(
            parameters = requestParams.toString(),
            expectedResponse = expectedResponseListOf(
              builder().from(abcConstructionApprentice)
                .withDistanceInMiles(null)
                .buildCandidateMatchingListItemResponseBody(),
              builder().from(amazonForkliftOperator)
                .withDistanceInMiles(null)
                .buildCandidateMatchingListItemResponseBody(),
              builder().from(tescoWarehouseHandler)
                .withDistanceInMiles(null)
                .buildCandidateMatchingListItemResponseBody(),
            ),
          )
        }

        @Test
        fun `return National Jobs list`() {
          requestParams.append("&isNationalJob=true")
          assertGetMatchingCandidateJobsIsOK(
            parameters = requestParams.toString(),
            expectedResponse = expectedResponseListOf(
              builder().from(abcNationalConstructionApprentice)
                .withDistanceInMiles(null)
                .buildCandidateMatchingListItemResponseBody(),
              builder().from(amazonNationalForkliftOperator)
                .withDistanceInMiles(null)
                .buildCandidateMatchingListItemResponseBody(),
            ),
          )
        }
      }
    }

    @Nested
    @DisplayName("And a regular valid release area postcode has been provided")
    inner class AndReleaseAreaPostcodeHasBeenProvided {
      @BeforeEach
      fun setUp() {
        requestParams.append("&releaseArea=$RELEASE_AREA_POSTCODE")
      }

      @Test
      fun `return Jobs list with calculated distance`() {
        assertGetMatchingCandidateJobsIsOK(
          parameters = requestParams.toString(),
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
      @DisplayName("And a search radius has been provided")
      inner class AndSearchRadiusHasBeenProvided {
        @BeforeEach
        fun setUp() {
          val searchRadiusInMiles = 20
          requestParams.append("&searchRadius=$searchRadiusInMiles")
        }

        @Test
        fun `return Jobs filtered by search radius`() {
          assertGetMatchingCandidateJobsIsOK(
            parameters = requestParams.toString(),
            expectedResponse = expectedResponseListOf(
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
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                tescoWarehouseHandler.candidateMatchingListItemResponseBody,
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
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                builder().from(amazonForkliftOperator)
                  .withDistanceInMiles(20.0f)
                  .buildCandidateMatchingListItemResponseBody(),
                tescoWarehouseHandler.candidateMatchingListItemResponseBody,
              ),
            )
          }
        }

        @Nested
        @DisplayName("And a custom pagination has been set")
        inner class CustomPagination {
          @BeforeEach
          fun setUp() {
            requestParams.append("&page=1&size=1")
          }

          @Test
          fun `return a custom paginated matching candidate Jobs list`() {
            assertGetMatchingCandidateJobsIsOK(
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                size = 1,
                page = 1,
                totalElements = 2,
                builder().from(tescoWarehouseHandler)
                  .withDistanceInMiles(1.0f)
                  .buildCandidateMatchingListItemResponseBody(),
              ),
            )
          }

          @Test
          fun `return a custom paginated National Jobs list`() {
            requestParams.append("&isNationalJob=true")
            assertGetMatchingCandidateJobsIsOK(
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                size = 1,
                page = 1,
                totalElements = 2,
                builder().from(amazonNationalForkliftOperator)
                  .withDistanceInMiles(null)
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
            requestParams.append("&sectors=retail")

            assertGetMatchingCandidateJobsIsOK(
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                builder().from(amazonForkliftOperator)
                  .withDistanceInMiles(20.0f)
                  .buildCandidateMatchingListItemResponseBody(),
              ),
            )
          }

          @Test
          fun `return Jobs filtered by various job sectors`() {
            requestParams.append("&sectors=retail,warehousing")

            assertGetMatchingCandidateJobsIsOK(
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                tescoWarehouseHandler.candidateMatchingListItemResponseBody,
                builder().from(amazonForkliftOperator)
                  .withDistanceInMiles(20.0f)
                  .buildCandidateMatchingListItemResponseBody(),
              ),
            )
          }

          @Test
          fun `return National Jobs filtered by various job sectors`() {
            requestParams.append("&isNationalJob=true&sectors=retail,warehousing")
            assertGetMatchingCandidateJobsIsOK(
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                builder().from(amazonNationalForkliftOperator)
                  .withDistanceInMiles(null)
                  .buildCandidateMatchingListItemResponseBody(),
              ),
            )
          }
        }

        @Nested
        @DisplayName("And a custom sorting order has been set")
        inner class AndOrderHasBeenSet {
          @Test
          fun `return Jobs list sorted by job title, in ascending order`() {
            requestParams.append("&sortBy=jobTitle&sortOrder=asc")

            assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
              parameters = requestParams.toString(),
              expectedJobTitlesSorted = listOf(
                "Forklift operator",
                "Warehouse handler",
              ),
            )
          }

          @Test
          fun `return Jobs sorted by job title, in descending order`() {
            requestParams.append("&sortBy=jobTitle&sortOrder=desc")

            assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
              parameters = requestParams.toString(),
              expectedJobTitlesSorted = listOf(
                "Warehouse handler",
                "Forklift operator",
              ),
            )
          }

          @Test
          fun `return Jobs sorted by closing date, in ascending order, by default`() {
            requestParams.append("&sortBy=closingDate")

            assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
              parameters = requestParams.toString(),
              expectedSortingOrder = "asc",
            )
          }

          @Test
          fun `return Jobs sorted by closing date, in ascending order`() {
            requestParams.append("&sortBy=closingDate&sortOrder=asc")

            assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
              parameters = requestParams.toString(),
              expectedSortingOrder = "asc",
            )
          }

          @Test
          fun `return Jobs sorted by closing date, in descending order`() {
            requestParams.append("&sortBy=closingDate&sortOrder=desc")

            assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
              parameters = requestParams.toString(),
              expectedSortingOrder = "desc",
            )
          }

          @Test
          fun `return Jobs sorted by distance, in ascending order`() {
            val order = "asc"
            requestParams.append("&sortBy=distance&sortOrder=$order")

            assertGetMatchingCandidateJobsIsOKAndSortedByDistance(
              parameters = requestParams.toString(),
              expectedSortingOrder = order,
            )
          }

          @Test
          fun `return Jobs sorted by distance, in descending order`() {
            val order = "desc"
            requestParams.append("&sortBy=distance&sortOrder=$order")

            assertGetMatchingCandidateJobsIsOKAndSortedByDistance(
              parameters = requestParams.toString(),
              expectedSortingOrder = order,
            )
          }

          @Test
          fun `return National Jobs sorted by job title, in descending order`() {
            requestParams.append("&isNationalJob=true&sortBy=jobTitle&sortOrder=desc")
            assertGetMatchingCandidateJobsIsOK(
              parameters = requestParams.toString(),
              expectedResponse = expectedResponseListOf(
                builder().from(amazonNationalForkliftOperator)
                  .withDistanceInMiles(null)
                  .buildCandidateMatchingListItemResponseBody(),
                builder().from(abcNationalConstructionApprentice)
                  .withDistanceInMiles(null)
                  .buildCandidateMatchingListItemResponseBody(),
              ),
            )
          }
        }
      }
    }
  }

  @Nested
  @DisplayName("Given not all mandatory request parameters have been provided")
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
