package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator

class ExpressionsOfInterestPutShould : ExpressionsOfInterestTestCase() {

  @Nested
  @DisplayName("Given a job has been created")
  inner class GivenAJobCreated {
    @Test
    fun `create ExpressionOfInterest with valid Job ID and prisoner's prisonNumber, when it does NOT exist`() {
      val jobId = obtainJobIdGivenAJobIsJustCreated()

      assertAddExpressionOfInterest(
        jobId = jobId,
        expectedStatus = CREATED,
        matchRedirectedUrl = true,
      )
    }

    @Nested
    @DisplayName("And Expression of Interest has been created, for given prisoner")
    inner class AndExpressionOfInterestCreated {
      private lateinit var jobId: String
      private lateinit var prisonNumber: String
      private val expectedJob = amazonForkliftOperator

      @BeforeEach
      fun setUp() {
        obtainJodIdAndPrisonNumberGivenAJobWithExpressionsOfInterestedJustCreated().let { jobIdAndPrisonNumber ->
          jobId = jobIdAndPrisonNumber[0]
          prisonNumber = jobIdAndPrisonNumber[1]
        }
      }

      @Test
      fun `do NOT create ExpressionOfInterest, when it exists`() {
        assertAddExpressionOfInterest(
          jobId = jobId,
          prisonNumber = prisonNumber,
          expectedStatus = OK,
        )
      }
    }
  }

  @Nested
  @DisplayName("Given no job")
  inner class GivenNoJob {
    @Test
    fun `do NOT create ExpressionOfInterest with non-existent job, and return error`() {
      val nonExistentJobId = randomUUID()

      assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
        jobId = nonExistentJobId,
        expectedResponse = """
          {
            "status": 400,
            "errorCode": null,
            "userMessage": "Illegal Argument: Job not found: jobId=$nonExistentJobId",
            "developerMessage": "Job not found: jobId=$nonExistentJobId",
            "moreInfo": null
          }
        """.trimIndent(),
      )
    }
  }

  @Test
  fun `do NOT create ExpressionOfInterest with invalid Job ID, and return error`() {
    val invalidJobID = invalidUUID
    assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
      jobId = invalidJobID,
      expectedResponse = """
        {
          "status": 400,
          "errorCode": null,
          "userMessage": "Validation failure: create.jobId: Invalid UUID format",
          "developerMessage": "create.jobId: Invalid UUID format",
          "moreInfo": null
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `do NOT create ExpressionOfInterest with invalid prisoner's prisonNumber, and return error`() {
    val jobId = obtainJobIdGivenAJobIsJustCreated()

    assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
      jobId = jobId,
      prisonNumber = invalidPrisonNumber,
      expectedResponse = """
        {
            "status": 400,
            "errorCode": null,
            "userMessage": "Validation failure: create.prisonNumber: size must be between 1 and 7",
            "developerMessage": "create.prisonNumber: size must be between 1 and 7",
            "moreInfo": null
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `do NOT create ExpressionOfInterest with empty prisoner's prisonNumber, and return error`() {
    val jobId = obtainJobIdGivenAJobIsJustCreated()

    assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
      jobId = jobId,
      prisonNumber = "",
      expectedStatus = NOT_FOUND,
    )
  }

  @Test
  fun `do NOT create ExpressionOfInterest without prisoner's prisonNumber, and return error`() {
    assertAddExpressionOfInterestRepliesNotFoundError(
      jobId = randomUUID(),
    )
  }
}
