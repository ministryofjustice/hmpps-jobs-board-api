package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.candidateMatchingItemListResponseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

class ExpressedInterestGetShould : ExpressedInterestTestCase() {
  @Test
  fun `return BAD REQUEST error when prison number is missing`() {
    assertGetExpressedInterestReturnsBadRequestError(
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

  @Nested
  @DisplayName("Given no jobs closing soon")
  inner class GivenNoJobs {
    @Test
    fun `return an empty job list of interest`() {
      assertGetExpressedInterestIsOk(
        prisonNumber = prisonNumber,
        expectedResponses = expectedResponseListOf(),
      )
    }
  }

  @Nested
  @DisplayName("Given some jobs have been created")
  inner class GivenSomeJobs {
    private val anotherPrisonNumber = "X1234YZ"

    private lateinit var allJobs: List<Job>
    private lateinit var postcodes: Array<String>
    private lateinit var distances: Map<EntityId, Double>
    private lateinit var releaseAreaPostcode: String

    private lateinit var tescoWarehouseHandler: Job
    private lateinit var amazonForkliftOperator: Job
    private lateinit var abcConstructionApprentice: Job

    @BeforeEach
    fun setUp() {
      postcodes = listOf("M4 5BD", "NW1 6XE", "NG1 1AA").map { it.replace(" ", "") }.toTypedArray()
      releaseAreaPostcode = postcodes.first()
      with(JobMother) {
        allJobs = listOf(tescoWarehouseHandler, amazonForkliftOperator, abcConstructionApprentice)
          .mapIndexed { i, it -> builder().from(it).apply { this.postcode = postcodes[i] }.build() }
      }
      tescoWarehouseHandler = allJobs[0]
      amazonForkliftOperator = allJobs[1]
      abcConstructionApprentice = allJobs[2]
      distances = mapOf(
        tescoWarehouseHandler.id to 0.0,
        amazonForkliftOperator.id to 161.2,
        abcConstructionApprentice.id to 58.0,
      )

      givenJobsAreCreated(*allJobs.toTypedArray())
    }

    @Test
    fun `return an empty job list of interest`() {
      assertGetExpressedInterestIsOk(
        prisonNumber = prisonNumber,
        expectedResponses = expectedResponseListOf(),
      )
    }

    @Nested
    @DisplayName("And some interests have been expressed by the prisoner")
    inner class AndHasExpressedInterest {
      private lateinit var expectedJobs: List<Job>

      @BeforeEach
      fun setUp() {
        expressInterestToJobs(prisonNumber, *allJobs.toTypedArray())
        expectedJobs = allJobs.map { builder().from(it).withExpressionOfInterestFrom(prisonNumber).build() }
      }

      @Test
      fun `return jobs list of interest, for given prisoner`() {
        val expectedResponses = expectedResponses(expectedJobs)
        assertGetExpressedInterestIsOk(
          prisonNumber = prisonNumber,
          releaseArea = releaseAreaPostcode,
          expectedResponses = expectedResponseListOf(*expectedResponses.toTypedArray()),
        )
      }

      @Nested
      @DisplayName("And a job has been archived, for the given prisoner")
      inner class AndHasArchivedJob {
        @BeforeEach
        fun setUp() {
          archiveJobs(prisonNumber, abcConstructionApprentice)
        }

        @Test
        fun `return jobs list of interest, excluding archived job, for given prisoner`() {
          val expectedResponses = expectedJobs.filter {
            when (it.employer.name) {
              tesco.name, amazon.name -> true
              else -> false
            }
          }.let { expectedResponses(it) }

          assertGetExpressedInterestIsOk(
            prisonNumber = prisonNumber,
            releaseArea = releaseAreaPostcode,
            expectedResponses = expectedResponseListOf(*expectedResponses.toTypedArray()),
          )
        }
      }

      @Nested
      @DisplayName("And a custom pagination has been set")
      inner class CustomPagination {
        @Test
        fun `return a custom paginated jobs list of interest`() {
          val expectedResponses = expectedJobs.filter { it.employer.name.equals(tesco.name) }
            .let { expectedResponses(it) }

          assertGetExpressedInterestIsOk(
            parameters = "prisonNumber=$prisonNumber&releaseArea=$releaseAreaPostcode&page=1&size=1",
            expectedResponse = expectedResponseListOf(
              size = 1,
              page = 1,
              totalElements = 3,
              *expectedResponses.toTypedArray(),
            ),
          )
        }
      }

      @Nested
      @DisplayName("And a custom sorting order has been set")
      inner class AndOrderHasBeenSet {
        @Test
        fun `return Jobs sorted by job title and employer name, in ascending order`() {
          assertGetExpressedInterestIsOKAndSortedByJobAndEmployer(
            parameters = "prisonNumber=$prisonNumber&sortBy=jobAndEmployer&sortOrder=asc",
            expectedJobTitlesSorted = listOf(
              "Apprentice plasterer",
              "Forklift operator",
              "Warehouse handler",
            ),
            expectedEmployerNameSortedList = listOf(
              "ABC Construction",
              "Amazon",
              "Tesco",
            ),
          )
        }

        @Test
        fun `return Jobs sorted by job title and employer name, in descending order`() {
          assertGetExpressedInterestIsOKAndSortedByJobAndEmployer(
            parameters = "prisonNumber=$prisonNumber&sortBy=jobAndEmployer&sortOrder=desc",
            expectedJobTitlesSorted = listOf(
              "Warehouse handler",
              "Forklift operator",
              "Apprentice plasterer",
            ),
            expectedEmployerNameSortedList = listOf(
              "Tesco",
              "Amazon",
              "ABC Construction",
            ),
          )
        }

        @Test
        fun `return Jobs sorted by closing date, in ascending order, by default`() {
          assertGetExpressedInterestIsOKAndSortedByClosingDate(
            parameters = "prisonNumber=$prisonNumber&sortBy=closingDate",
            expectedSortingOrder = "asc",
          )
        }

        @Test
        fun `return Jobs sorted by closing date, in ascending order`() {
          assertGetExpressedInterestIsOKAndSortedByClosingDate(
            parameters = "prisonNumber=$prisonNumber&sortBy=closingDate&sortOrder=asc",
            expectedSortingOrder = "asc",
          )
        }

        @Test
        fun `return Jobs sorted by closing date, in descending order`() {
          assertGetExpressedInterestIsOKAndSortedByClosingDate(
            parameters = "prisonNumber=$prisonNumber&sortBy=closingDate&sortOrder=desc",
            expectedSortingOrder = "desc",
          )
        }

        @Test
        fun `return Jobs sorted by location, without releaseArea specified`() {
          assertGetExpressedInterestIsOKAndSortedByLocation(
            parameters = "prisonNumber=$prisonNumber&sortBy=location&sortOrder=asc",
            expectedDistanceSortedList = List(3) { null },
          )
        }

        @Test
        fun `return Jobs sorted by location, in ascending order`() {
          val expectedDistance = distances.values.sorted()
          assertGetExpressedInterestIsOKAndSortedByLocation(
            parameters = "prisonNumber=$prisonNumber&releaseArea=$releaseAreaPostcode&sortBy=location&sortOrder=asc",
            expectedDistanceSortedList = expectedDistance,
          )
        }

        @Test
        fun `return Jobs sorted by location, in descending order`() {
          val expectedDistance = distances.values.sortedDescending()
          assertGetExpressedInterestIsOKAndSortedByLocation(
            parameters = "prisonNumber=$prisonNumber&releaseArea=$releaseAreaPostcode&sortBy=location&sortOrder=desc",
            expectedDistanceSortedList = expectedDistance,
          )
        }
      }
    }

    @Nested
    @DisplayName("And another prisoner has expressed interest to jobs")
    inner class AndAnotherPrisonerHasExpressedInterest {
      private lateinit var expectedJobs: List<Job>
      private lateinit var expectedTescoWarehouseHandler: Job
      private lateinit var expectedAmazonForkliftOperator: Job
      private lateinit var expectedAbcConstructionApprentice: Job

      @BeforeEach
      fun setUp() {
        expressInterestToJobs(prisonNumber, tescoWarehouseHandler, abcConstructionApprentice)
        expressInterestToJobs(anotherPrisonNumber, *allJobs.toTypedArray())

        expectedTescoWarehouseHandler = builder().from(tescoWarehouseHandler)
          .withExpressionOfInterestFrom(prisonNumber)
          .withExpressionOfInterestFrom(anotherPrisonNumber).build()
        expectedAmazonForkliftOperator =
          builder().from(amazonForkliftOperator).withExpressionOfInterestFrom(anotherPrisonNumber).build()
        expectedAbcConstructionApprentice = builder().from(abcConstructionApprentice)
          .withExpressionOfInterestFrom(prisonNumber)
          .withExpressionOfInterestFrom(anotherPrisonNumber).build()

        expectedJobs =
          listOf(expectedTescoWarehouseHandler, expectedAmazonForkliftOperator, expectedAbcConstructionApprentice)
      }

      @Test
      fun `return jobs list of interest, excluding job of interest from another prisoner`() {
        val expectedResponses = expectedResponses(expectedTescoWarehouseHandler, expectedAbcConstructionApprentice)

        assertGetExpressedInterestIsOk(
          prisonNumber = prisonNumber,
          releaseArea = releaseAreaPostcode,
          expectedResponses = expectedResponseListOf(*expectedResponses.toTypedArray()),
        )
      }

      @Test
      fun `return jobs list of interest, including job archived for another prisoner`() {
        archiveJobs(anotherPrisonNumber, abcConstructionApprentice)

        val expectedResponses = expectedResponses(expectedTescoWarehouseHandler, expectedAbcConstructionApprentice)

        assertGetExpressedInterestIsOk(
          prisonNumber = prisonNumber,
          releaseArea = releaseAreaPostcode,
          expectedResponses = expectedResponseListOf(*expectedResponses.toTypedArray()),
        )
      }
    }

    private fun expectedResponses(jobs: List<Job>) =
      expectedResponses(prisonNumber, *jobs.toTypedArray())

    private fun expectedResponses(vararg jobs: Job): List<String> = expectedResponses(prisonNumber, *jobs)

    private fun expectedResponses(prisonNumber: String, vararg jobs: Job): List<String> {
      return jobs.map {
        it.candidateMatchingItemListResponseBody(prisonNumber, distances[it.id]!!)
      }
    }
  }

  private fun assertGetExpressedInterestIsOk(
    prisonNumber: String,
    releaseArea: String? = null,
    expectedResponses: String? = null,
  ) {
    val parametersBuilder = StringBuilder("prisonNumber=$prisonNumber")
    releaseArea?.let { parametersBuilder.append("&releaseArea=$releaseArea") }
    assertGetExpressedInterestIsOk(
      parameters = "$parametersBuilder",
      expectedResponse = expectedResponses,
    )
  }
}
