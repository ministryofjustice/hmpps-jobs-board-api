package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.amazon
import java.util.*

const val EXPRESSIONS_OF_INTEREST_PATH_PREFIX = "expressions-of-interest"

abstract class ExpressionsOfInterestTestCase : JobsTestCase() {

  protected val invalidPrisonNumber = "A0000AAZ"
  protected val nonExistentPrisonNumber = "Z9999AA"
  protected val invalidUUID = "00000000-0000-0000-0000-00000"

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

  protected fun obtainJobIdGivenAJobIsJustCreated(): String {
    assertAddEmployerIsCreated(employer = amazon)
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
