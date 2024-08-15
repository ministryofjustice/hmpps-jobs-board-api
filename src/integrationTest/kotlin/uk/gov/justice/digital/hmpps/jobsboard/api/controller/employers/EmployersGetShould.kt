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
  fun `retrieve a paginated empty list of Employers when none registered`() {
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
  fun `retrieve a default paginated Employers list filtered by name`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployerIsOK(
      parameters = "name=tesco",
      expectedResponse = expectedResponseListOf(tescoBody),
    )
  }

  @Test
  fun `retrieve a default paginated list of Employers filtered by incomplete name when filtered is applied`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployerIsOK(
      parameters = "name=tes",
      expectedResponse = expectedResponseListOf(tescoBody),
    )
  }

  @Test
  fun `retrieve a default paginated list of Employers filtered by sector when filtered is applied`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = amazonBody)

    assertGetEmployerIsOK(
      parameters = "sector=logistics",
      expectedResponse = expectedResponseListOf(amazonBody),
    )
  }

  @Test
  fun `retrieve a default paginated list of Employers filtered by name AND sector when filters are applied`() {
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
  fun `retrieve a list of Employers filtered by incomplete name AND sector when filters are applied with mixed cases`() {
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
  fun `retrieve second page of Employers list filtered by name AND sector when filters and custom pagination are applied`() {
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
  fun `retrieve a default sorted by name in ascendent order Employers list`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployersIsOKAndSortedByName(
      expectedNamesSorted = listOf("Sainsbury's", "Tesco"),
    )
  }

  @Test
  fun `retrieve an Employers list sorted by name in descendent order when sorting applied`() {
    assertAddEmployerIsCreated(body = tescoBody)
    assertAddEmployerIsCreated(body = sainsburysBody)

    assertGetEmployersIsOKAndSortedByName(
      parameters = "sortBy=name&sortOrder=desc",
      expectedNamesSorted = listOf("Tesco", "Sainsbury's"),
    )
  }

  @Test
  fun `retrieve an Employers list sorted by date added in default ascendent order when sort-by applied`() {
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
  fun `retrieve an Employers list sorted by date added in descendent order when sort-by and sort-order applied`() {
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
