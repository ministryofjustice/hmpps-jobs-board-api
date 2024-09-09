package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import java.util.*

const val EXPRESSIONS_OF_INTEREST_PATH_PREFIX = "expressions-of-interest"

abstract class ExpressionsOfInterestTestCase : JobsTestCase() {

  protected val invalidPrisonNumber = "A0000AAZ"
  protected val nonExistentPrisonNumber = "Z9999AA"

  protected fun assertAddExpressionOfInterest(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = CREATED,
    matchRedirectedUrl: Boolean = false,
  ): Array<String> = assertEditExpressionOfInterest(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = PUT,
    matchRedirectedUrl = matchRedirectedUrl,
  )

  protected fun assertAddExpressionOfInterestThrowsValidationOrIllegalArgumentError(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = BAD_REQUEST,
    expectedResponse: String? = null,
  ) = assertEditExpressionOfInterestThrowsValidationError(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = PUT,
    expectedResponse = expectedResponse,
  )

  protected fun assertAddExpressionOfInterestRepliesNotFoundError(
    jobId: String,
  ) = assertRequestWithoutBody(
    url = "$JOBS_ENDPOINT/$jobId/$EXPRESSIONS_OF_INTEREST_PATH_PREFIX/",
    expectedStatus = NOT_FOUND,
    expectedHttpVerb = PUT,
  )

  protected fun assertDeleteExpressionOfInterest(
    jobId: String,
    prisonNumber: String,
    expectedStatus: HttpStatus = NO_CONTENT,
  ): Array<String> = assertEditExpressionOfInterest(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = DELETE,
  )

  protected fun assertDeleteExpressionOfInterestRepliesNotFoundError(
    jobId: String,
  ) = assertRequestWithoutBody(
    url = "$JOBS_ENDPOINT/$jobId/$EXPRESSIONS_OF_INTEREST_PATH_PREFIX/",
    expectedStatus = NOT_FOUND,
    expectedHttpVerb = DELETE,
  )

  private fun assertEditExpressionOfInterestThrowsValidationError(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = BAD_REQUEST,
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
    matchRedirectedUrl: Boolean = false,
  ): Array<String> {
    val finalJobId = jobId ?: randomUUID()
    val finalPrisonNumber = prisonNumber ?: randomPrisonNumber()
    val url = "$JOBS_ENDPOINT/$finalJobId/$EXPRESSIONS_OF_INTEREST_PATH_PREFIX/$finalPrisonNumber"
    assertRequestWithoutBody(
      url = url,
      expectedStatus = expectedStatus,
      expectedHttpVerb = expectedHttpVerb,
      expectedResponse = expectedResponse,
      expectedRedirectUrlPattern = if (matchRedirectedUrl) "http*://*$url" else null,
    )
    return arrayOf(finalJobId, finalPrisonNumber)
  }

  protected fun randomUUID(): String = UUID.randomUUID().toString()

  protected fun randomPrisonNumber(): String =
    randomAlphabets(1) + randomDigits(4) + randomAlphabets(2)

  protected fun obtainJobIdGivenAJobIsJustCreated(): String {
    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )
    assertAddJobIsCreated(body = amazonForkliftOperatorJobBody).also { jobId ->
      return jobId
    }
  }

  protected fun obtainJodIdAndPrisonNumberGivenAJobWithExpressionsOfInterestedJustCreated(): Array<String> {
    val jobId = obtainJobIdGivenAJobIsJustCreated()
    val jobIdAndPrisonNumber = assertAddExpressionOfInterest(jobId)
    return jobIdAndPrisonNumber
  }
}
