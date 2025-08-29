package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.itemListResponseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.jobCreator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.responseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import java.util.UUID

class JobsGetShould : JobsTestCase() {
  @Test
  fun `retrieve an existing Job`() {
    assertAddEmployerIsCreated(employer = amazon)
    val jobId = assertAddJobIsCreated(job = amazonForkliftOperator)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = amazonForkliftOperator.responseBody,
    )
  }

  @Test
  fun `retrieve an existing Job with all optional fields empty`() {
    assertAddEmployerIsCreated(employer = abcConstruction)
    val jobId = assertAddJobIsCreated(job = abcConstructionApprentice)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = abcConstructionApprentice.responseBody,
    )
  }

  @Test
  fun `return a Job with all optional fields empty as null`() {
    assertAddEmployerIsCreated(employer = tesco)
    val jobId = assertAddJobIsCreated(job = tescoWarehouseHandler)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = tescoWarehouseHandler.responseBody,
    )
  }

  @Test
  fun `returns Not Found when a Job doesn't exist`() {
    val nonExistingJobId = UUID.randomUUID().toString()
    assertGetJobThrowsNotFoundError(
      jobId = nonExistingJobId,
      errorMessage = "Job with Id $nonExistingJobId not found",
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
        amazonForkliftOperator.itemListResponseBody,
        tescoWarehouseHandler.itemListResponseBody,
        abcConstructionApprentice.itemListResponseBody,
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
        amazonForkliftOperator.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Job title`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=Forklift operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperator.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Job title`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=operator",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperator.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by full Employer name`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=Tesco",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by incomplete Employer name`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "jobTitleOrEmployerName=Tes",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "sector=retail",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperator.itemListResponseBody,
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
        abcConstructionApprentice.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by Job creator`() {
    givenThreeJobsAreCreated()

    val createdBy = jobCreator.lowercase()
    assertGetJobsIsOK(
      parameters = "createdBy=$createdBy",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperator.itemListResponseBody,
        tescoWarehouseHandler.itemListResponseBody,
        abcConstructionApprentice.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by Job creator AND Job title OR Employer name`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "createdBy=$jobCreator&jobTitleOrEmployerName=tesco",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by Job creator AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "createdBy=$jobCreator&sector=construction",
      expectedResponse = expectedResponseListOf(
        abcConstructionApprentice.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list filtered by Job creator AND Job title OR Employer name AND job sector`() {
    givenThreeJobsAreCreated()

    assertGetJobsIsOK(
      parameters = "createdBy=$jobCreator&jobTitleOrEmployerName=tesco&sector=wareHOUSING",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.itemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated empty Jobs list filtered by unknown Job creator`() {
    givenThreeJobsAreCreated()

    val unknownJobCreator = "unknown_creator"
    assertGetJobsIsOK(
      parameters = "createdBy=$unknownJobCreator",
      expectedResponse = expectedResponseListOf(),
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
        tescoWarehouseHandler.itemListResponseBody,
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
