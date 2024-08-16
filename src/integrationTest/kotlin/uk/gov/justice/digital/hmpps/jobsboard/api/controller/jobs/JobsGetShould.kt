package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED
import java.util.*

class JobsGetShould : JobsTestCase() {

  @Test
  fun `retrieve an existing Job`() {
    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    val jobId = assertAddJobIsCreated(body = amazonForkliftOperatorJobBody)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = amazonForkliftOperatorJobResponse(jobCreationTime),
    )
  }

  @Test
  fun `return null on empty optional fields`() {
    assertAddEmployer(
      id = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    val jobId = assertAddJobIsCreated(body = tescoWarehouseHandlerJobBody)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = tescoWarehouseHandlerJobBody,
    )
  }

  @Test
  fun `retrieve a default paginated empty Jobs list`() {
    assertGetJobIsOK(
      expectedResponse = expectedResponseListOf(),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list`() {
    givenTwoJobsAreRegistered()

    assertGetJobIsOK(
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a custom paginated Jobs list`() {
    givenTwoJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "page=1&size=1",
      expectedResponse = expectedResponseListOf(
        size = 1,
        page = 1,
        totalElements = 2,
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Job title`() {
    givenTwoJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Forklift operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Job title`() {
    givenTwoJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Employer name`() {
    givenTwoJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Tesco",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Employer name`() {
    givenTwoJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Tes",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by job sector`() {
    givenTwoJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "sector=retail",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime)
      )
    )
  }

  private fun givenTwoJobsAreRegistered() {
    assertAddEmployer(
      id = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    assertAddJobIsCreated(
      body = tescoWarehouseHandlerJobBody,
    )

    assertAddJobIsCreated(
      body = amazonForkliftOperatorJobBody,
    )
  }
}
