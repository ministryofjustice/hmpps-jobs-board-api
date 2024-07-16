package uk.gov.justice.digital.hmpps.jobsboard.api

import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import java.time.LocalDateTime

class EmployerControllerShould : ApplicationTestCase() {

  @Test
  fun `create a valid Employer`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/1a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )
  }

  @Test
  fun `not create an Employer with invalid UUID`() {
    assertErrorRequestWithBody(
      method = PUT,
      endpoint = "/employers/invalid-uuid",
      body = tescoBody,
      expectedStatus = BAD_REQUEST,
      expectedErrorResponse = """
        {
          "status":400,
          "errorCode":null,
          "userMessage":"Validation failure: save.id: Invalid UUID format",
          "developerMessage":"save.id: Invalid UUID format",
          "moreInfo":null
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `update an existing Employer`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = sainsburysBody,
      expectedStatus = OK,
    )

    assertResponse(
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      expectedStatus = OK,
      expectedResponse = sainsburysBody,
    )
  }

  @Test
  fun `retrieve an existing Employer`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      expectedStatus = OK,
      expectedResponse = tescoBody,
    )
  }

  @Test
  fun `retrieve a paginated empty list of Employers when none registered`() {
    assertResponse(
      endpoint = "/employers",
      expectedStatus = OK,
      expectedResponse = """
        {
          "content": [ ],
          "page": {
            "size": 10,
            "number": 0,
            "totalElements": 0,
            "totalPages": 0
          }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve a default paginated Employers list`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers",
      expectedStatus = OK,
      expectedResponse = """
        {
          "content": [ $tescoBody, $sainsburysBody ],
          "page": {
            "size": 10,
            "number": 0,
            "totalElements": 2,
            "totalPages": 1
          }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve second page of paginated list of Employers when page and size are informed`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers?page=1&size=1",
      expectedStatus = OK,
      expectedResponse = """
          {
            "content": [ $tescoBody ],
            "page": {
              "size": 1,
              "number": 1,
              "totalElements": 2,
              "totalPages": 2
            }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve a default paginated list of Employers filtered by name when filtered is applied`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers?name=tesco",
      expectedStatus = OK,
      expectedResponse = """
          {
            "content": [ $tescoBody ],
            "page": {
              "size": 10,
              "number": 0,
              "totalElements": 1,
              "totalPages": 1
            }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve a default paginated list of Employers filtered by incomplete name when filtered is applied`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers?name=tes",
      expectedStatus = OK,
      expectedResponse = """
          {
            "content": [ $tescoBody ],
            "page": {
              "size": 10,
              "number": 0,
              "totalElements": 1,
              "totalPages": 1
            }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve a default paginated list of Employers filtered by sector when filtered is applied`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers?sector=logistics",
      expectedStatus = OK,
      expectedResponse = """
          {
            "content": [ $amazonBody ],
            "page": {
              "size": 10,
              "number": 0,
              "totalElements": 1,
              "totalPages": 1
            }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve a default paginated list of Employers filtered by name AND sector when filters are applied`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/2aff5cfe-ffdd-4a52-b672-26638e7be060",
      body = tescoLogisticsBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0f9d76ab-55e9-411c-8def-24eeb734c83f",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers?name=Tesco&sector=LOGISTICS",
      expectedStatus = OK,
      expectedResponse = """
          {
            "content": [ $tescoLogisticsBody ],
            "page": {
              "size": 10,
              "number": 0,
              "totalElements": 1,
              "totalPages": 1
            }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve a list of Employers filtered by incomplete name AND sector when filters are applied with mixed cases`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/2aff5cfe-ffdd-4a52-b672-26638e7be060",
      body = tescoLogisticsBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0f9d76ab-55e9-411c-8def-24eeb734c83f",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers?name=sAINS&sector=retail",
      expectedStatus = OK,
      expectedResponse = """
          {
            "content": [ $sainsburysBody ],
            "page": {
              "size": 10,
              "number": 0,
              "totalElements": 1,
              "totalPages": 1
            }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve second page of Employers list filtered by name AND sector when filters and custom pagination are applied`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/2aff5cfe-ffdd-4a52-b672-26638e7be060",
      body = tescoLogisticsBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0f9d76ab-55e9-411c-8def-24eeb734c83f",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    assertResponse(
      endpoint = "/employers?name=Sainsbury's&sector=RETAIL&page=0&size=1",
      expectedStatus = OK,
      expectedResponse = """
          {
            "content": [ $sainsburysBody ],
            "page": {
              "size": 1,
              "number":0,
              "totalElements": 1,
              "totalPages": 1
            }
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `retrieve a default sorted by name in ascendent order Employers list`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertSortedByNameResponse(
      endpoint = "/employers",
      expectedStatus = OK,
      expectedOrderedContentNames = listOf("Sainsbury's", "Tesco"),
    )
  }

  @Test
  fun `retrieve an Employers list sorted by name in descendent order when sorting applied`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertSortedByNameResponse(
      endpoint = "/employers?sortBy=name&sortOrder=desc",
      expectedStatus = OK,
      expectedOrderedContentNames = listOf("Tesco", "Sainsbury's"),
    )
  }

  @Test
  fun `retrieve an Employers list sorted by date added in default ascendent order when sort-by applied`() {
    val fixedTime = LocalDateTime.of(2024, 7, 1, 1, 0, 0)
    whenever(timeProvider.now())
      .thenReturn(fixedTime)
      .thenReturn(fixedTime.plusDays(1))

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertSortedByDateResponse(
      endpoint = "/employers?sortBy=createdAt",
      expectedStatus = OK,
      expectedOrderedContentDates = listOf("2024-07-01T01:00:00", "2024-07-02T01:00:00"),
    )
  }

  @Test
  fun `retrieve an Employers list sorted by date added in descendent order when sort-by and sort-order applied`() {
    val fixedTime = LocalDateTime.of(2024, 7, 1, 1, 0, 0)
    whenever(timeProvider.now())
      .thenReturn(fixedTime)
      .thenReturn(fixedTime.plusDays(1))

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/0fec6332-0839-4a4a-9c15-b86c06e1ca03",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/e82fd9e6-ffcf-410c-a7c8-ffeb50da3f18",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )

    assertSortedByDateResponse(
      endpoint = "/employers?sortBy=createdAt&sortOrder=desc",
      expectedStatus = OK,
      expectedOrderedContentDates = listOf("2024-07-02T01:00:00", "2024-07-01T01:00:00"),
    )
  }

  val tescoBody: String = """
        {
          "name": "Tesco",
          "description": "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
          "sector": "RETAIL",
          "status": "SILVER"
        }
  """.trimIndent()

  val tescoLogisticsBody: String = """
        {
          "name": "Tesco",
          "description": "This is another Tesco employer that provides logistic services.",
          "sector": "LOGISTICS",
          "status": "GOLD"
        }
  """.trimIndent()

  val sainsburysBody = """
        {
          "name": "Sainsbury's",
          "description": "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
          "sector": "RETAIL",
          "status": "GOLD"
        }
  """.trimIndent()

  val amazonBody = """
        {
          "name": "Amazon",
          "description": "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence",
          "sector": "LOGISTICS",
          "status": "KEY_PARTNER"
        }
  """.trimIndent()
}
