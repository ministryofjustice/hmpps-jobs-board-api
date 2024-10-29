package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator

class ExpressionsOfInterestDeleteShould : ExpressionsOfInterestTestCase() {

  @Nested
  @DisplayName("Given a job has been created")
  inner class GivenAJobCreated {
    @Nested
    @DisplayName("And Interest has been expressed, for given prisoner")
    inner class AndInterestExpressed {
      private lateinit var jobId: String
      private lateinit var prisonNumber: String
      private val expectedJob = amazonForkliftOperator

      @BeforeEach
      fun setUp() {
        givenAJobIsCreatedWithExpressionOfInterest().let { jobIdAndPrisonNumber ->
          jobId = jobIdAndPrisonNumber[0]
          prisonNumber = jobIdAndPrisonNumber[1]
        }
      }

      @Test
      fun `delete ExpressionOfInterest, when it exists`() {
        assertDeleteExpressionOfInterest(
          jobId = jobId,
          prisonNumber = prisonNumber,
          expectedStatus = NO_CONTENT,
        )
      }

      @Test
      fun `do NOT delete ExpressionOfInterest, when it does NOT exist, and return error`() {
        assertDeleteExpressionOfInterest(
          jobId = jobId,
          prisonNumber = nonExistentPrisonNumber,
          expectedStatus = NOT_FOUND,
        )
      }
    }
  }

  @Nested
  @DisplayName("Given no job")
  inner class GivenNoJob {
    @Test
    fun `do NOT delete ExpressionOfInterest with non-existent job, and return error`() {
      assertDeleteExpressionOfInterest(
        jobId = randomUUID(),
        prisonNumber = randomPrisonNumber(),
        expectedStatus = BAD_REQUEST,
      )
    }
  }

  @Test
  fun `do NOT delete ExpressionOfInterest with invalid Job ID, and return error`() {
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
  fun `do NOT delete ExpressionOfInterest without prisoner's prisonNumber, and return error`() {
    assertDeleteExpressionOfInterestRepliesNotFoundError(
      jobId = randomUUID(),
    )
  }

  private fun givenAJobIsCreatedWithExpressionOfInterest(): Array<String> {
    val jobId = obtainJobIdGivenAJobIsJustCreated()
    assertAddExpressionOfInterest(jobId = jobId, prisonNumber = prisonNumber)
    return arrayOf(jobId, prisonNumber)
  }
}
