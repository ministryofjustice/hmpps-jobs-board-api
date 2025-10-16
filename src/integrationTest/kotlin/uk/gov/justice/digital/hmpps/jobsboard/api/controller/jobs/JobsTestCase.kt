package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcNationalConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonNationalForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.time.Instant
import java.util.*

const val JOBS_ENDPOINT = "/jobs"

class JobsTestCase : EmployerTestCase() {
  val jobCreationTime: Instant = Instant.parse("2024-01-01T00:00:00Z")
  final val prisonNumber = "A1234BC"
  val anotherPrisonNumber = "A5678BC"

  @BeforeEach
  override fun setup() {
    super.setup()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobCreationTime))
  }

  protected fun assertAddJobIsCreated(
    job: Job,
  ): String = assertAddJob(
    id = job.id.id,
    body = job.requestBody,
    expectedStatus = CREATED,
  )

  protected fun assertUpdateJobIsOk(
    jobId: String,
    body: String,
  ): String = assertAddJob(
    id = jobId,
    body = body,
    expectedStatus = OK,
  )

  protected fun assertAddJobThrowsValidationError(
    jobId: String? = null,
    body: String,
    expectedResponse: String,
  ) {
    assertAddJob(
      id = jobId,
      body = body,
      expectedStatus = BAD_REQUEST,
      expectedResponse = expectedResponse,
    )
  }

  private fun assertAddJob(
    id: String? = null,
    body: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
  ): String {
    val finalJobId = id ?: randomUUID().toString()
    assertRequestWithBody(
      url = "$JOBS_ENDPOINT/$finalJobId",
      body = body,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
    return finalJobId
  }

  protected fun assertGetJobIsOK(
    jobId: String,
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = JOBS_ENDPOINT
    jobId.let { url = "$url/$it" }
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetJobsIsOK(
    parameters: String? = null,
    expectedResponse: String,
  ) {
    var url = JOBS_ENDPOINT
    parameters?.let { url = "$url?$it" }
    assertResponse(
      url = url,
      expectedStatus = OK,
      expectedResponse = expectedResponse,
    )
  }

  protected fun assertGetJobThrowsNotFoundError(
    jobId: String,
    errorMessage: String,
  ) {
    assertResponse(
      url = "$JOBS_ENDPOINT/$jobId",
      expectedStatus = NOT_FOUND,
      expectedResponse = """
        {
          "status":404,
          "errorCode":null,
          "userMessage":"No Job found failure: $errorMessage",
          "developerMessage":"$errorMessage",
          "moreInfo":null
        }
      """.trimIndent(),
    )
  }

  protected fun assertGetJobsIsOKAndSortedByJobTitle(
    parameters: String? = "",
    expectedJobTitlesSorted: List<String>,
  ) {
    assertResponse(
      url = "$JOBS_ENDPOINT?$parameters",
      expectedStatus = OK,
      expectedJobTitleSortedList = expectedJobTitlesSorted,
    )
  }

  protected fun assertGetJobsIsOKAndSortedByDate(
    parameters: String? = null,
    expectedSortingOrder: String = "asc",
  ) {
    assertResponse(
      url = "$JOBS_ENDPOINT?$parameters",
      expectedStatus = OK,
      expectedDateSortingOrder = expectedSortingOrder,
    )
  }

  protected fun givenThreeJobsAreCreated() {
    givenJobsAreCreated(
      tescoWarehouseHandler,
      amazonForkliftOperator,
      abcConstructionApprentice,
    )
  }

  protected fun givenRegionalAndNationalJobsAreCreated() {
    givenJobsAreCreated(
      tescoWarehouseHandler,
      amazonForkliftOperator,
      abcConstructionApprentice,
      amazonNationalForkliftOperator,
      abcNationalConstructionApprentice,
    )
  }

  protected fun givenJobsAreCreated(vararg jobs: Job) {
    val employers = jobs.map { it.employer }.distinctBy { it.id }
    employers.forEach { employer -> assertAddEmployerIsCreated(employer) }
    jobs.forEach { job -> assertAddJobIsCreated(job) }
  }

  protected fun String.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }

  protected fun List<String>.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }
}
