package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobsTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository

const val APPLICATIONS_ENDPOINT = "/applications"

@Transactional(propagation = Propagation.NOT_SUPPORTED)
abstract class ApplicationsTestCase : JobsTestCase() {
  @Autowired
  private lateinit var employerRepository: EmployerRepository

  @AfterEach
  fun tearDown() {
    employerRepository.deleteAll()
  }

  protected fun assertAddApplication(
    application: Application,
    expectedStatus: HttpStatus = CREATED,
    expectedResponse: String? = null,
  ) = assertAddOrUpdateApplication(application.id.id, application.requestBody, expectedStatus, expectedResponse)

  protected fun assertUpdateApplication(
    application: Application,
    expectedStatus: HttpStatus = OK,
    expectedResponse: String? = null,
  ) = assertAddOrUpdateApplication(application.id.id, application.requestBody, expectedStatus, expectedResponse)

  protected fun assertAddApplicationFailedAsBadRequest(
    application: Application,
    expectedErrorMessage: String,
    expectedDeveloperMessage: String? = expectedErrorMessage,
  ) {
    application.let {
      assertAddApplication(
        application = it,
        expectedStatus = BAD_REQUEST,
        expectedResponse = """
            { "status":400,"errorCode":null,"userMessage":"Illegal Argument: $expectedErrorMessage","developerMessage":"${expectedDeveloperMessage ?: expectedErrorMessage}" }
        """.trimIndent(),
      )
    }
  }

  private fun assertAddOrUpdateApplication(
    applicationId: String,
    requestBody: String,
    expectedStatus: HttpStatus = OK,
    expectedResponse: String? = null,
  ) {
    assertRequestWithBody(
      url = "$APPLICATIONS_ENDPOINT/$applicationId",
      body = requestBody,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
  }
}
