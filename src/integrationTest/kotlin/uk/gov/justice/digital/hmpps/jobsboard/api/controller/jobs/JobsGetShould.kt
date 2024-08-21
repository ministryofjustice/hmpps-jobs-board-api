package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
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
  fun `retrieve an existing Job with all optional fields empty`() {
    assertAddEmployer(
      id = "182e9a24-6edb-48a6-a84f-b7061f004a97",
      body = abcConstructionBody,
      expectedStatus = CREATED,
    )

    val jobId = assertAddJobIsCreated(body = abcConstructionJobBody)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = abcConstructionJobResponse(jobCreationTime),
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
    givenThreeJobsAreCreated()

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
    givenThreeJobsAreCreated()

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
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Forklift operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Job title`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Employer name`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Tesco",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Employer name`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Tes",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "sector=retail",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by Job title OR Employer name AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=tesco&sector=retail",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Job title OR Employer name AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=tEs&sector=retail",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a custom paginated Jobs list filtered by Job title OR Employer name AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOK(
      parameters = "jobTitleOrEmployerName=Tesco&sector=retail&page=0&size=1",
      expectedResponse = expectedResponseListOf(
        size = 1,
        page = 0,
        totalElements = 2,
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by job title, in ascending order, by default`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOKAndSortedByJobTitle(
      expectedJobTitlesSorted = listOf(
        "Apprentice plasterer",
        "Forklift operator",
        "Warehouse handler",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by job title, in ascending order`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOKAndSortedByJobTitle(
      parameters = "sortBy=jobTitle&sortOrder=asc",
      expectedJobTitlesSorted = listOf(
        "Apprentice plasterer",
        "Forklift operator",
        "Warehouse handler",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by job title in, descending order`() {
    givenThreeJobsAreCreated()

    assertGetJobIsOKAndSortedByJobTitle(
      parameters = "sortBy=jobTitle&sortOrder=desc",
      expectedJobTitlesSorted = listOf(
        "Warehouse handler",
        "Forklift operator",
        "Apprentice plasterer",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by creation date, in ascending order, by default`() {
    givenJobsMustHaveDifferentCreationTimes()

    givenThreeJobsAreCreated()

    assertGetJobIsOKAndSortedByDate(
      parameters = "sortBy=createdAt",
      expectedDatesSorted = listOf(
        "2024-01-01T00:00:00Z",
        "2024-01-01T00:00:10Z",
        "2024-01-01T00:00:20Z",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by creation date, in ascending order`() {
    givenJobsMustHaveDifferentCreationTimes()

    givenThreeJobsAreCreated()

    assertGetJobIsOKAndSortedByDate(
      parameters = "sortBy=createdAt&sortOrder=asc",
      expectedDatesSorted = listOf(
        "2024-01-01T00:00:00Z",
        "2024-01-01T00:00:10Z",
        "2024-01-01T00:00:20Z",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by creation date, in descending order`() {
    givenJobsMustHaveDifferentCreationTimes()

    givenThreeJobsAreCreated()

    assertGetJobIsOKAndSortedByDate(
      parameters = "sortBy=createdAt&sortOrder=desc",
      expectedDatesSorted = listOf(
        "2024-01-01T00:00:20Z",
        "2024-01-01T00:00:10Z",
        "2024-01-01T00:00:00Z",
      ),
    )
  }

  private fun givenJobsMustHaveDifferentCreationTimes() {
    whenever(dateTimeProvider.now)
      .thenReturn(Optional.of(jobCreationTime))
      .thenReturn(Optional.of(jobCreationTime.plusSeconds(10)))
      .thenReturn(Optional.of(jobCreationTime.plusSeconds(20)))
  }

  private fun givenThreeJobsAreCreated() {
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
