package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

class EmployersGetShould : EmployerTestCase() {
  @Test
  fun `retrieve an existing Employer`() {
    val employerId = assertAddEmployerIsCreated(body = tescoBody)

    assertGetEmployerIsOK(
      employerId = employerId,
      expectedResponse = tescoBody,
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
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployerIsOK(
      expectedResponse = expectedResponseListOf(tescoBody, sainsburysBody),
    )
  }

  @Test
  fun `retrieve a custom paginated Employers list`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    this.assertGetEmployerIsOK(
      parameters = "page=1&size=1",
      expectedResponse = expectedResponseListOf(size = 1, page = 1, totalElements = 2, tescoBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by full name`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployerIsOK(
      parameters = "name=tesco",
      expectedResponse = expectedResponseListOf(tescoBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by incomplete name`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployerIsOK(
      parameters = "name=tes",
      expectedResponse = expectedResponseListOf(tescoBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by industry sector`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = amazonBody)

    assertGetEmployerIsOK(
      parameters = "sector=logistics",
      expectedResponse = expectedResponseListOf(amazonBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by name AND sector`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = tescoLogisticsBody)
    assertAddEmployerIsCreated(body = sainsburysBody)
    assertAddEmployerIsCreated(body = amazonBody)

    assertGetEmployerIsOK(
      parameters = "name=Tesco&sector=LOGISTICS",
      expectedResponse = expectedResponseListOf(tescoLogisticsBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list filtered by incomplete name AND sector`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = tescoLogisticsBody)
    assertAddEmployerIsCreated(body = sainsburysBody)
    assertAddEmployerIsCreated(body = amazonBody)

    assertGetEmployerIsOK(
      parameters = "name=sAINS&sector=retail",
      expectedResponse = expectedResponseListOf(sainsburysBody),
    )
  }

  @Test
  fun `retrieve a custom paginated Employers list filtered by name AND sector`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = tescoLogisticsBody)
    assertAddEmployerIsCreated(body = sainsburysBody)
    assertAddEmployerIsCreated(body = amazonBody)

    assertGetEmployerIsOK(
      parameters = "name=Sainsbury's&sector=RETAIL&page=0&size=1",
      expectedResponse = expectedResponseListOf(size = 1, page = 0, sainsburysBody),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by name, in ascending order, by default`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployersIsOKAndSortedByName(
      expectedNamesSorted = listOf("Sainsbury's", "Tesco"),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by name, in descending order`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployersIsOKAndSortedByName(
      parameters = "sortBy=name&sortOrder=desc",
      expectedNamesSorted = listOf("Tesco", "Sainsbury's"),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by creation date, in ascending order, by default`() {
    val fixedTime = LocalDateTime.of(2024, 7, 1, 1, 0, 0)
    whenever(timeProvider.now())
      .thenReturn(fixedTime)
      .thenReturn(fixedTime.plusDays(1))

    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployersIsOkAndSortedByDate(
      parameters = "sortBy=createdAt",
      expectedDatesSorted = listOf("2024-07-01T01:00:00", "2024-07-02T01:00:00"),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list sorted by creation date, in descending order`() {
    val fixedTime = LocalDateTime.of(2024, 7, 1, 1, 0, 0)
    whenever(timeProvider.now())
      .thenReturn(fixedTime)
      .thenReturn(fixedTime.plusDays(1))

    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployersIsOkAndSortedByDate(
      parameters = "sortBy=createdAt&sortOrder=desc",
      expectedDatesSorted = listOf("2024-07-02T01:00:00", "2024-07-01T01:00:00"),
    )
  }
}
