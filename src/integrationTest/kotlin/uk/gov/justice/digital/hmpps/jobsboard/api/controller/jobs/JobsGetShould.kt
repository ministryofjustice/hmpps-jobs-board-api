package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.abcConstructionBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.amazonBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.tescoBody

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
    assertGetJobsIsOK(
      expectedResponse = expectedResponseListOf(),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
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

    assertGetJobsIsOK(
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

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=Forklift operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Job title`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Employer name`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=Tesco",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Employer name`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=Tes",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "sector=retail",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperatorJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by Job title OR Employer name AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=tesco&sector=retail",
      expectedResponse = expectedResponseListOf(),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Job title OR Employer name AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=er&sector=construction",
      expectedResponse = expectedResponseListOf(
        abcConstructionJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a custom paginated Jobs list filtered by Job title OR Employer name AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=Tesco&sector=warehousing&page=0&size=1",
      expectedResponse = expectedResponseListOf(
        size = 1,
        page = 0,
        totalElements = 1,
        tescoWarehouseHandlerJobItemListResponse(jobCreationTime),
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by job title, in ascending order, by default`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOKAndSortedByJobTitle(
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

    assertGetJobsIsOKAndSortedByJobTitle(
      parameters = "sortBy=jobTitle&sortOrder=asc",
      expectedJobTitlesSorted = listOf(
        "Apprentice plasterer",
        "Forklift operator",
        "Warehouse handler",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by job title, in descending order`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOKAndSortedByJobTitle(
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

    assertGetJobsIsOKAndSortedByDate(
      parameters = "sortBy=createdAt",
      expectedSortingOrder = "asc",
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by creation date, in ascending order`() {
    val sortingOrder = "asc"
    givenJobsMustHaveDifferentCreationTimes()

    givenThreeJobsAreCreated()

    assertGetJobsIsOKAndSortedByDate(
      parameters = "sortBy=createdAt&sortOrder=$sortingOrder",
      expectedSortingOrder = sortingOrder,
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list sorted by creation date, in descending order`() {
    val sortingOrder = "desc"
    givenJobsMustHaveDifferentCreationTimes()

    givenThreeJobsAreCreated()

    assertGetJobsIsOKAndSortedByDate(
      parameters = "sortBy=createdAt&sortOrder=$sortingOrder",
      expectedSortingOrder = sortingOrder,
    )
  }

  private fun givenJobsMustHaveDifferentCreationTimes() {
    givenCurrentTimeIsStrictlyIncreasing(jobCreationTime)
  }
}
