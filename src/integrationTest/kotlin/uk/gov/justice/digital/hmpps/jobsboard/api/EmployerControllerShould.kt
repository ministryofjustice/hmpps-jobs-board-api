package uk.gov.justice.digital.hmpps.jobsboard.api

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK

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
  fun `retrieve paginated empty list of all Employers`() {
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
  fun `retrieve the second page of paginated list of all Employers`() {
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
            "content": [ $sainsburysBody ],
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

  val tescoBody: String = """
        {
          "name": "Tesco",
          "description": "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
          "sector": "RETAIL",
          "status": "SILVER"
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
}
