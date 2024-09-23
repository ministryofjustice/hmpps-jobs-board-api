package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.newJobItemListResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.time.Instant
import java.util.*

const val JOBS_ENDPOINT = "/jobs"

class JobsTestCase : EmployerTestCase() {
  val jobCreationTime = Instant.parse("2024-01-01T00:00:00Z")
  val prisonNumber = "A1234BC"

  @BeforeEach
  override fun setup() {
    super.setup()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobCreationTime))
  }

  protected fun assertAddJobIsCreated(
    job: Job,
  ): String {
    return assertAddJob(
      id = job.id.id,
      body = job.requestBody,
      expectedStatus = CREATED,
    )
  }

  protected fun assertUpdateJobIsOk(
    jobId: String,
    body: String,
  ): String {
    return assertAddJob(
      id = jobId,
      body = body,
      expectedStatus = OK,
    )
  }

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
    jobId?.let { url = "$url/$it" }
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
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = amazon)
    assertAddEmployerIsCreated(employer = abcConstruction)

    assertAddJobIsCreated(job = tescoWarehouseHandler)
    assertAddJobIsCreated(job = amazonForkliftOperator)
    assertAddJobIsCreated(job = abcConstructionApprentice)

    assertAddExpressionOfInterest("6fdf2bf4-cfe6-419c-bab2-b3673adbb393", prisonNumber)
  }

  protected fun abcConstructionJobItemListResponse(createdAt: Instant): String = newJobItemListResponse(
    employerId = "182e9a24-6edb-48a6-a84f-b7061f004a97",
    employerName = "ABC Construction",
    jobTitle = "Apprentice plasterer",
    numberOfVacancies = 3,
    sector = "CONSTRUCTION",
    createdAt = createdAt.toString(),
  )

  protected fun tescoWarehouseHandlerJobItemListResponse(createdAt: Instant): String = newJobItemListResponse(
    employerId = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
    employerName = "Tesco",
    jobTitle = "Warehouse handler",
    numberOfVacancies = 1,
    sector = "WAREHOUSING",
    createdAt = createdAt.toString(),
  )

  protected fun amazonForkliftOperatorJobItemListResponse(createdAt: Instant): String = newJobItemListResponse(
    employerId = "bf392249-b360-4e3e-81a0-8497047987e8",
    employerName = "Amazon",
    jobTitle = "Forklift operator",
    numberOfVacancies = 2,
    sector = "RETAIL",
    createdAt = createdAt.toString(),
  )

  protected fun String.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }

  protected fun List<String>.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }
}
