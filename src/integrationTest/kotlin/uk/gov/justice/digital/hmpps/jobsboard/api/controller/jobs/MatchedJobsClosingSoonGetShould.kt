package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.closingSoonListResponseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

@DisplayName("Matched jobs closing soon GET Should")
class MatchedJobsClosingSoonGetShould : MatchedJobClosingSoonTestCase() {
  @Test
  fun `return BAD REQUEST error when prison number is missing`() {
    assertGetMatchedJobClosingSoonReturnsBadRequestError(
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
    fun `return an empty list of jobs`() {
      assertGetMatchedJobClosingSoonIsOk(
        parameters = "prisonNumber=$prisonNumber",
        expectedResponse = "[]",
      )
    }
  }

  @Nested
  @DisplayName("Given some jobs closing soon")
  inner class GivenJobsClosingSoon {
    private val allJobs = listOf(tescoWarehouseHandler, amazonForkliftOperator, abcConstructionApprentice)
    private val anotherPrisonNumber = "X1234YZ"

    @BeforeEach
    fun setUp() {
      givenThreeJobsAreCreated()
    }

    @Test
    fun `return a list of jobs closing soon`() {
      assertGetMatchedJobClosingSoonIsOk(prisonNumber, expectedJobs = allJobs)
    }

    @Nested
    @DisplayName("And a job has been archived for the given prisoner")
    inner class AndHasAJobArchived {
      @BeforeEach
      fun setUp() {
        assertAddArchived(
          jobId = tescoWarehouseHandler.id.id,
          prisonNumber = prisonNumber,
        )
      }

      @Test
      fun `return list of jobs closing soon, excluding archived job`() {
        val expectedJobs = listOf(amazonForkliftOperator, abcConstructionApprentice)
        assertGetMatchedJobClosingSoonIsOk(prisonNumber, expectedJobs = expectedJobs)
      }

      @Test
      fun `return list of jobs closing soon, of another prisoner, including job archived for given prisoner`() {
        assertGetMatchedJobClosingSoonIsOk(anotherPrisonNumber, expectedJobs = allJobs)
      }
    }

    @Nested
    @DisplayName("And interest has been expressed by the prisoner")
    inner class AndHasExpressedInterest {
      @BeforeEach
      fun setUp() {
        assertAddExpressionOfInterest(
          jobId = tescoWarehouseHandler.id.id,
          prisonNumber = prisonNumber,
        )
      }

      @Test
      fun `return list of jobs closing soon, excluding job of interest, for given prisoner`() {
        val expectedJobs = listOf(amazonForkliftOperator, abcConstructionApprentice)
        assertGetMatchedJobClosingSoonIsOk(prisonNumber, expectedJobs = expectedJobs)
      }

      @Test
      fun `return list of jobs closing soon, of another prisoner, including job archived for given prisoner`() {
        assertGetMatchedJobClosingSoonIsOk(anotherPrisonNumber, expectedJobs = allJobs)
      }
    }

    @Nested
    @DisplayName("And custom filter has been specified")
    inner class CustomFilter {
      @Test
      fun `return jobs closing soon filtered by job sectors`() {
        val sectors = listOf("CONSTRUCTION", "Retail")
        val expectedJobs = listOf(amazonForkliftOperator, abcConstructionApprentice)
        assertGetMatchedJobClosingSoonIsOk(prisonNumber, sectors = sectors, expectedJobs = expectedJobs)
      }

      @Test
      fun `return top jobs closing soon, of specified size`() {
        val size = 1
        val expectedJobs = listOf(amazonForkliftOperator)
        assertGetMatchedJobClosingSoonIsOk(prisonNumber, size = size, expectedJobs = expectedJobs)
      }
    }

    private fun assertGetMatchedJobClosingSoonIsOk(
      prisonNumber: String,
      sectors: List<String>? = null,
      size: Int? = null,
      expectedJobs: List<Job>? = null,
    ) {
      val parametersBuilder = StringBuilder("prisonNumber=$prisonNumber")
      sectors?.let { parametersBuilder.append("&sectors=${sectors.joinToString(separator = ",")}") }
      size?.let { parametersBuilder.append("&size=$size") }
      assertGetMatchedJobClosingSoonIsOk(
        parameters = "$parametersBuilder",
        expectedResponse = expectedJobs?.let { jobsToListResponse(it) },
      )
    }

    private fun jobsToListResponse(jobs: List<Job>) = jobs.map { it.closingSoonListResponseBody }.joinToString().let { "[$it]" }
  }
}
