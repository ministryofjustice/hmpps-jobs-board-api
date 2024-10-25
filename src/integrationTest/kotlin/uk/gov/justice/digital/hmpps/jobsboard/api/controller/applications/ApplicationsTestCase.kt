package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonABC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonXYZ
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
    userMessagePrefix: String? = "Illegal Argument",
  ) {
    application.let {
      assertAddApplication(
        application = it,
        expectedStatus = BAD_REQUEST,
        expectedResponse = """
            { "status":400,"errorCode":null,"userMessage":"$userMessagePrefix: $expectedErrorMessage","developerMessage":"${expectedDeveloperMessage ?: expectedErrorMessage}" }
        """.trimIndent(),
      )
    }
  }

  protected fun assertGetApplicationsIsOk(parameters: String? = null, expectedResponse: String? = null) =
    assertGetApplications(parameters, OK, expectedResponse)

  protected fun assertGetApplicationsFailedAsBadRequest(
    parameters: String? = null,
    expectedErrorMessage: String,
    expectedDeveloperMessage: String? = expectedErrorMessage,
    userMessagePrefix: String? = "Missing required parameter",
  ) = """
    { "status":400,"errorCode":null,"userMessage":"$userMessagePrefix: $expectedErrorMessage","developerMessage":"${expectedDeveloperMessage ?: expectedErrorMessage}" }
  """.trimIndent().let { assertGetApplications(parameters, BAD_REQUEST, it) }

  protected fun givenThreeApplicationsAreCreated() = givenApplicationsAreCreated(*applicationsFromPrisonMDI.toTypedArray())

  protected fun givenMoreApplicationsFromMultiplePrisons() {
    (applicationsFromPrisonMDI + applicationsFromPrisonABC + applicationsFromPrisonXYZ).toTypedArray()
      .let { applications ->
        givenApplicationsAreCreated(*applications)
      }
  }

  protected fun givenApplicationsAreCreated(vararg applications: Application) {
    val jobs = applications.map { it.job }.toSet()
    val employers = jobs.map { it.employer }.toSet()

    employers.forEach { assertAddEmployerIsCreated(it) }
    jobs.forEach { assertAddJobIsCreated(it) }
    applications.forEach { assertAddApplicationIsCreated(it) }
  }

  protected val Application.searchResponseBody get() = applicationResponseBody(this)

  private fun applicationResponseBody(application: Application): String {
    return application.let {
      """
      {
        "id": "${it.id}",
        "jobId": "${it.job.id}",
        "prisonNumber": "${it.prisonNumber}",
        "prisonId": "${it.prisonId}",
        "firstName": "${it.firstName}",
        "lastName": "${it.lastName}",
        "employerName": "${it.job.employer.name}",
        "jobTitle": "${it.job.title}",
        "applicationStatus": "${it.status}",
        "createdAt": "$jobCreationTime",
        "lastModifiedAt": "$jobCreationTime"
      }
      """.trimIndent()
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

  private fun assertGetApplications(
    parameters: String? = null,
    expectedStatus: HttpStatus = OK,
    expectedResponse: String? = null,
  ) {
    assertResponse(
      url = "$APPLICATIONS_ENDPOINT${parameters?.let { "?$it" } ?: ""}",
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
  }
}
