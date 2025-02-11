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

class ExpressedInterestClosingSoonGetShould : ExpressedInterestClosingSoonTestCase() {
  @Test
  fun `return BAD REQUEST error when prison number is missing`() {
    assertGetExpressedInterestClosingSoonReturnsBadRequestError(
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
      assertGetExpressedInterestClosingSoonIsOk(prisonNumber, emptyList())
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
    fun `return an empty job list of interest`() {
      assertGetExpressedInterestClosingSoonIsOk(prisonNumber, emptyList())
    }

    @Nested
    @DisplayName("And some interests have been expressed by the prisoner")
    inner class AndHasExpressedInterest {
      @BeforeEach
      fun setUp() {
        expressInterestToJobs(prisonNumber, *allJobs.toTypedArray())
        expressInterestToJobs(anotherPrisonNumber, tescoWarehouseHandler, abcConstructionApprentice)
      }

      @Test
      fun `return jobs list of interest closing soon, for given prisoner`() {
        val expectedJobs = allJobs
        assertGetExpressedInterestClosingSoonIsOk(prisonNumber, expectedJobs)
      }

      @Test
      fun `return jobs list of interest closing soon, excluding job of no interest, for another prisoner`() {
        val expectedJobs = listOf(tescoWarehouseHandler, abcConstructionApprentice)
        assertGetExpressedInterestClosingSoonIsOk(anotherPrisonNumber, expectedJobs)
      }

      @Nested
      @DisplayName("And a job has been archived, for the given prisoner")
      inner class AndHasArchivedJob {
        @BeforeEach
        fun setUp() {
          archiveJobs(prisonNumber, abcConstructionApprentice)
        }

        @Test
        fun `return jobs list of interest closing soon, excluding archived job, for given prisoner`() {
          val expectedJobs = listOf(tescoWarehouseHandler, amazonForkliftOperator)
          assertGetExpressedInterestClosingSoonIsOk(prisonNumber, expectedJobs)
        }

        @Test
        fun `return jobs list of interest closing soon, including job archived for others, for another prisoner`() {
          val expectedJobs = listOf(tescoWarehouseHandler, abcConstructionApprentice)
          assertGetExpressedInterestClosingSoonIsOk(anotherPrisonNumber, expectedJobs)
        }
      }
    }
  }

  private fun assertGetExpressedInterestClosingSoonIsOk(
    prisonNumber: String,
    expectedJobs: List<Job>? = null,
  ) {
    val parametersBuilder = StringBuilder("prisonNumber=$prisonNumber")
    assertGetExpressedInterestClosingSoonIsOk(
      parameters = "$parametersBuilder",
      expectedResponse = expectedJobs?.let { jobsToListResponse(it) },
    )
  }

  private fun jobsToListResponse(jobs: List<Job>) = jobs.map { it.closingSoonListResponseBody }.joinToString().let { "[$it]" }
}
