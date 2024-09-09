package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT

class ExpressionsOfInterestDeleteShould : ExpressionsOfInterestTestCase() {
  val prisonNumber = "A1234BC"

  @Test
  fun `delete ExpressionOfInterest, when it exists`() {
    val ids = givenAJobIsCreatedWithExpressionOfInterest()
    val jobId = ids[0]
    val prisonNumber = ids[1]

    assertDeleteExpressionOfInterest(
      jobId = jobId,
      prisonNumber = prisonNumber,
      expectedStatus = NO_CONTENT,
    )
  }

  @Test
  fun `do NOT delete ExpressionOfInterest, when it does NOT exist, and return error`() {
    val jobId = givenAJobIsCreatedWithExpressionOfInterest().first()

    assertDeleteExpressionOfInterest(
      jobId = jobId,
      prisonNumber = nonExistentPrisonNumber,
      expectedStatus = NOT_FOUND,
    )
  }

  @Test
  fun `do NOT delete ExpressionOfInterest with non-existent job, and return error`() {
    assertDeleteExpressionOfInterest(
      jobId = randomUUID(),
      prisonNumber = randomPrisonNumber(),
      expectedStatus = BAD_REQUEST,
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
