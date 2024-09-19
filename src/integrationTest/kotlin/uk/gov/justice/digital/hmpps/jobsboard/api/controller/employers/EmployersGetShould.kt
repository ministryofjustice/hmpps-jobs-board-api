package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.sainsburys
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployersMother.tescoLogistics

class EmployersGetShould : EmployerTestCase() {
  @Test
  fun `retrieve an existing Employer`() {
    val employerId = assertAddEmployerIsCreated(employer = tesco)

    assertGetEmployerIsOK(
      employerId = employerId,
      expectedResponse = tesco.requestBody,
    )
  }

  @Test
  fun `retrieve a default paginated empty Employers list`() {
    assertGetEmployerIsOK(
      expectedResponse = expectedResponseListOf(),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployerIsOK(
      expectedResponse = expectedResponseListOf(tesco.requestBody, sainsburys.requestBody),
    )
  }

  @Test
  fun `retrieve a custom paginated Employers list`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    this.assertGetEmployerIsOK(
      parameters = "page=1&size=1",
      expectedResponse = expectedResponseListOf(size = 1, page = 1, totalElements = 2, tesco.requestBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by full name`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployerIsOK(
      parameters = "name=tesco",
      expectedResponse = expectedResponseListOf(tesco.requestBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by incomplete name`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployerIsOK(
      parameters = "name=tes",
      expectedResponse = expectedResponseListOf(tesco.requestBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by industry sector`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = amazon)

    assertGetEmployerIsOK(
      parameters = "sector=logistics",
      expectedResponse = expectedResponseListOf(amazon.requestBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by name AND sector`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = tescoLogistics)
    assertAddEmployerIsCreated(employer = sainsburys)
    assertAddEmployerIsCreated(employer = amazon)

    assertGetEmployerIsOK(
      parameters = "name=Tesco&sector=LOGISTICS",
      expectedResponse = expectedResponseListOf(tescoLogistics.requestBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by incomplete name AND sector`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = tescoLogistics)
    assertAddEmployerIsCreated(employer = sainsburys)
    assertAddEmployerIsCreated(employer = amazon)

    assertGetEmployerIsOK(
      parameters = "name=sAINS&sector=retail",
      expectedResponse = expectedResponseListOf(sainsburys.requestBody),
    )
  }

  @Test
  fun `retrieve a custom paginated Employers list filtered by name AND sector`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = tescoLogistics)
    assertAddEmployerIsCreated(employer = sainsburys)
    assertAddEmployerIsCreated(employer = amazon)

    assertGetEmployerIsOK(
      parameters = "name=Sainsbury's&sector=RETAIL&page=0&size=1",
      expectedResponse = expectedResponseListOf(size = 1, page = 0, sainsburys.requestBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by name, in ascending order, by default`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployersIsOKAndSortedByName(
      expectedNamesSorted = listOf("Sainsbury's", "Tesco"),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by name, in ascending order`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployersIsOKAndSortedByName(
      parameters = "sortBy=name&sortOrder=asc",
      expectedNamesSorted = listOf("Sainsbury's", "Tesco"),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by name, in descending order`() {
    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployersIsOKAndSortedByName(
      parameters = "sortBy=name&sortOrder=desc",
      expectedNamesSorted = listOf("Tesco", "Sainsbury's"),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by creation date, in ascending order, by default`() {
    givenEmployersHaveIncreasingIncrementByDayCreationTimes()

    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployersIsOkAndSortedByDate(
      parameters = "sortBy=createdAt",
      expectedSortingOrder = "asc",
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by creation date, in ascending order`() {
    val sortingOrder = "asc"
    givenEmployersHaveIncreasingIncrementByDayCreationTimes()

    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployersIsOkAndSortedByDate(
      parameters = "sortBy=createdAt&sortOrder=$sortingOrder",
      expectedSortingOrder = sortingOrder,
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by creation date, in descending order`() {
    val sortingOrder = "desc"
    givenEmployersHaveIncreasingIncrementByDayCreationTimes()

    assertAddEmployerIsCreated(employer = tesco)
    assertAddEmployerIsCreated(employer = sainsburys)

    assertGetEmployersIsOkAndSortedByDate(
      parameters = "sortBy=createdAt&sortOrder=$sortingOrder",
      expectedSortingOrder = sortingOrder,
    )
  }

  private fun givenEmployersHaveIncreasingIncrementByDayCreationTimes() {
    givenCurrentTimeIsStrictlyIncreasingIncrementByDay(employerCreationTime)
  }
}
