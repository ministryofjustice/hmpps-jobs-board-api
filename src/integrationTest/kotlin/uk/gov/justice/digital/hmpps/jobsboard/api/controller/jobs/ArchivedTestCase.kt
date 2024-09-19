package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.amazonBody

const val ARCHIVED_PATH_PREFIX = "archived"

abstract class ArchivedTestCase : JobsTestCase() {

  protected val invalidPrisonNumber = "A0000AAZ"
  protected val nonExistentPrisonNumber = "Z9999AA"
  protected val invalidUUID = "00000000-0000-0000-0000-00000"

  protected fun assertAddArchivedThrowsValidationOrIllegalArgumentError(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = BAD_REQUEST,
    expectedResponse: String? = null,
  ) = assertRequestArchivedThrowsValidationError(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = PUT,
    expectedResponse = expectedResponse,
  )

  protected fun assertAddArchivedRepliesNotFoundError(
    jobId: String,
  ) = assertRequestWithoutBody(
    url = "$JOBS_ENDPOINT/$jobId/$ARCHIVED_PATH_PREFIX/",
    expectedStatus = NOT_FOUND,
    expectedHttpVerb = PUT,
  )

  protected fun assertDeleteArchived(
    jobId: String,
    prisonNumber: String,
    expectedStatus: HttpStatus = NO_CONTENT,
  ): Array<String> = assertRequestArchived(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = DELETE,
  )

  protected fun assertDeleteArchivedRepliesNotFoundError(
    jobId: String,
  ) = assertRequestWithoutBody(
    url = "$JOBS_ENDPOINT/$jobId/$ARCHIVED_PATH_PREFIX/",
    expectedStatus = NOT_FOUND,
    expectedHttpVerb = DELETE,
  )

  private fun assertRequestArchivedThrowsValidationError(
    jobId: String? = null,
    prisonNumber: String? = null,
    expectedStatus: HttpStatus = BAD_REQUEST,
    expectedHttpVerb: HttpMethod,
    expectedResponse: String? = null,
  ) = assertRequestArchived(
    jobId = jobId,
    prisonNumber = prisonNumber,
    expectedStatus = expectedStatus,
    expectedHttpVerb = expectedHttpVerb,
    expectedResponse = expectedResponse,
  )

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

  protected fun obtainJodIdAndPrisonNumberGivenAJobWithArchivedJustCreated(): Array<String> {
    val jobId = obtainJobIdGivenAJobIsJustCreated()
    val jobIdAndPrisonNumber = assertAddArchived(jobId)
    return jobIdAndPrisonNumber
  }
}
