package uk.gov.justice.digital.hmpps.jobsboard.api

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get

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

  @Disabled
  @Test
  fun `get all Jobs`() {
    val httpHeaders: HttpHeaders =
      this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDIT"))

    mockMvc.get("/job-board/employer/test/list?pageNo=0&pageSize=10&sortBy=id") {
      contentType = APPLICATION_JSON
      accept = APPLICATION_JSON
      headers {
        httpHeaders.forEach { (name, values) ->
          values.forEach { value ->
            header(name, value)
          }
        }
      }
    }.andExpect {
      status { isOk() }
      content {
        contentType(APPLICATION_JSON)
        json(
          """
        {
            "id": "1a553b0e-9d0b-46b2-bd78-3fa24b7232da",
            "name": "Saynsbury's",
            "description": "This is the employer BIO",
            "sector": "sector name",
            "status": "status"
        }
          """.trimIndent(),
        )
      }
    }
  }

  val tescoBody: String = """
        {
          "name": "Tesco",
          "description": "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
          "sector": "sector",
          "status": "status"
        }
  """.trimIndent()

  val sainsburysBody = """
        {
          "name": "Sainsbury's",
          "description": "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
          "sector": "sector",
          "status": "status"
        }
  """.trimIndent()
}
