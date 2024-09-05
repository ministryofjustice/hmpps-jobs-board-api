package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import java.util.*

class ExpressionsOfInterestPutShould : ExpressionsOfInterestTestCase() {
  @Test
  fun `create expression-of-interest with valid job-ID and prisoner's prison-number, when it does NOT exist`() {
    val jobId = obtainJobIdGivenAJobIsJustCreated()

    assertAddExpressionOfInterest(
      jobId = jobId,
      expectedStatus = CREATED,
    )
  }

  @Test
  fun `do NOT create expression-of-interest, when it exists`() {
    val jobIdAndPrisonNumber = obtainJodIdAndPrisonNumberGivenAJobWithExpressionsOfInterestedJustCreated()
    val jobId = jobIdAndPrisonNumber[0]
    val prisonNumber = jobIdAndPrisonNumber[1]

    assertAddExpressionOfInterest(
      jobId = jobId,
      prisonNumber = prisonNumber,
      expectedStatus = OK,
    )
  }

  @Test
  fun `do NOT create expression-of-interest with non-existent job, and return error`() {
    val nonExistentJobId = UUID.randomUUID().toString()

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

  @Test
  fun `do NOT create expression-of-interest with invalid prisoner's prison-number, and return error`() {
    val jobId = obtainJobIdGivenAJobIsJustCreated()

    assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
      jobId = jobId,
      prisonNumber = invalidPrisonNumber,
      expectedResponse = """
        {
            "status": 400,
            "errorCode": null,
            "userMessage": "Validation failure: create.prisonerPrisonNumber: size must be between 1 and 7",
            "developerMessage": "create.prisonerPrisonNumber: size must be between 1 and 7",
            "moreInfo": null
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `do NOT create expression-of-interest with empty prisoner's prison-number, and return error`() {
    val jobId = obtainJobIdGivenAJobIsJustCreated()

    assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
      jobId = jobId,
      prisonNumber = "",
      expectedStatus = NOT_FOUND,
    )
  }

  @Test
  fun `do NOT create expression-of-interest without prisoner's prison-number, and return error`() {
    val jobId = obtainJobIdGivenAJobIsJustCreated()
    assertAddExpressionOfInterestWithoutPrisonNumberRepliesNotFoundError(
      jobId = jobId,
    )
  }

  private fun obtainJobIdGivenAJobIsJustCreated(): String {
    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )
    assertAddJobIsCreated(body = amazonForkliftOperatorJobBody).also { jobId ->
      return jobId
    }
  }

  private fun obtainJodIdAndPrisonNumberGivenAJobWithExpressionsOfInterestedJustCreated(): Array<String> {
    val jobId = obtainJobIdGivenAJobIsJustCreated()
    val jobIdAndPrisonNumber = assertAddExpressionOfInterest(jobId)
    return jobIdAndPrisonNumber
  }

  private fun failAsNotImplemented(): Nothing = fail("Yet to implement")
}
