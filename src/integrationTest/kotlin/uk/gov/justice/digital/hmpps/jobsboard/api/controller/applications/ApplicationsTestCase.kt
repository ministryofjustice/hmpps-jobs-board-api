package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAbcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAmazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToTescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobsTestCase

const val APPLICATIONS_ENDPOINT = "/applications"

// @Transactional(propagation = Propagation.NOT_SUPPORTED)
abstract class ApplicationsTestCase : JobsTestCase() {

  @Autowired
  protected lateinit var applicationRepository: ApplicationRepository

  @BeforeEach
  override fun setup() {
    applicationRepository.deleteAll()
    super.setup()
  }

  protected fun assertAddApplicationIsCreated(
    application: Application,
    expectedResponse: String? = null,
  ) = assertAddApplication(application, CREATED, expectedResponse)

  protected fun assertUpdateApplicationIsOk(
    application: Application,
    expectedResponse: String? = null,
  ) = assertUpdateApplication(application, OK, expectedResponse)

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

  protected fun givenThreeApplicationsAreCreated() {
    givenApplicationsAreCreated(
      applicationToAmazonForkliftOperator,
      applicationToTescoWarehouseHandler,
      applicationToAbcConstructionApprentice,
    )
  }

  protected fun givenApplicationsAreCreated(vararg applications: Application) {
    applications.forEach {
      assertAddEmployerIsCreated(it.job.employer)
      assertAddJobIsCreated(it.job)
      assertAddApplicationIsCreated(it)
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
