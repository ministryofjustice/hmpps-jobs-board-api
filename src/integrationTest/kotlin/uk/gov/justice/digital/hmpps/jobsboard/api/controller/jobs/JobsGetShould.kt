package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED

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
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
        abcConstructionJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a custom paginated Jobs list`() {
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "page=1&size=1",
      expectedResponse = expectedResponseListOf(
        size = 1,
        page = 1,
        totalElements = 3,
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Job title`() {
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Forklift operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Job title`() {
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Employer name`() {
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Tesco",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Employer name`() {
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Tes",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by job sector`() {
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "sector=retail",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by Job title OR Employer name AND job sector`() {
    givenThreeJobsAreRegistered()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=tesco&sector=retail",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      )
    )
  }

  private fun givenThreeJobsAreRegistered() {
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

    assertAddEmployer(
      id = "182e9a24-6edb-48a6-a84f-b7061f004a97",
      body = abcConstructionBody,
      expectedStatus = CREATED,
    )

    assertAddJobIsCreated(
      body = tescoWarehouseHandlerJobBody,
    )

    assertAddJobIsCreated(
      body = amazonForkliftOperatorJobBody,
    )

    assertAddJobIsCreated(
      body = abcConstructionJobBody,
    )
  }
}
