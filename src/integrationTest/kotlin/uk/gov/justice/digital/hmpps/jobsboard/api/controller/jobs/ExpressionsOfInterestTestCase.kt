package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.UUID.randomUUID

const val EXPRESSIONS_OF_INTEREST_PATH_PREFIX = "expressions-of-interest"

abstract class ExpressionsOfInterestTestCase : JobsTestCase() {

  protected val invalidPrisonNumber = "A0000AAZ"

  protected fun assertAddExpressionOfInterest(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = HttpStatus.CREATED,
  ): Array<String> = assertEditExpressionOfInterest(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = HttpMethod.PUT,
  )

  protected fun assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    expectedResponse: String? = null,
  ) = assertEditExpressionOfInterestThrowsValidationError(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = HttpMethod.PUT,
    expectedResponse = expectedResponse,
  )

  protected fun assertAddExpressionOfInterestWithoutPrisonNumberRepliesNotFoundError(
    jobId: String,
  ) = assertRequestWithoutBody(
    url = "$JOBS_ENDPOINT/$jobId/$EXPRESSIONS_OF_INTEREST_PATH_PREFIX/",
    expectedStatus = HttpStatus.NOT_FOUND,
    expectedHttpVerb = HttpMethod.PUT,
  )

  private fun assertEditExpressionOfInterestThrowsValidationError(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    expectedHttpVerb: HttpMethod,
    expectedResponse: String? = null,
  ) = assertEditExpressionOfInterest(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = expectedHttpVerb,
    expectedResponse = expectedResponse,
  )

  private fun assertEditExpressionOfInterest(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus,
    expectedHttpVerb: HttpMethod,
    expectedResponse: String? = null,
  ): Array<String> {
    val finalJobId = jobId ?: randomUUID().toString()
    val finalPrisonNumber = prisonNumber ?: randomPrisonNumber()
    assertRequestWithoutBody(
      url = "$JOBS_ENDPOINT/$finalJobId/$EXPRESSIONS_OF_INTEREST_PATH_PREFIX/$finalPrisonNumber",
      expectedStatus = expectedStatus,
      expectedHttpVerb = expectedHttpVerb,
      expectedResponse = expectedResponse,
    )
    return arrayOf(finalJobId, finalPrisonNumber)
  }

  protected fun randomPrisonNumber(): String =
    randomAlphabets(1) + randomDigits(4) + randomAlphabets(2)
}
