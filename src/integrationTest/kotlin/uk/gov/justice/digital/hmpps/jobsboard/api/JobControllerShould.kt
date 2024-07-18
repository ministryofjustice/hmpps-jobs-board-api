package uk.gov.justice.digital.hmpps.jobsboard.api

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get

class JobControllerShould : ApplicationTestCase() {

  @Test
  fun `create a valid job`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/1a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )
    assertRequestWithBody(
      method = PUT,
      endpoint = "/candidate-matching/job/2a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      body = sainsburysJobBody,
      expectedStatus = CREATED,
    )
  }

  @Test
  fun `not create an Job with invalid UUID`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/1a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )
    assertErrorRequestWithBody(
      method = PUT,
      endpoint = "/candidate-matching/job/invalid-uuid",
      body = sainsburysJobBody,
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
  fun `update an existing Job`() {
    assertRequestWithBody(
      method = PUT,
      endpoint = "/employers/1a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      body = sainsburysBody,
      expectedStatus = CREATED,
    )
    assertRequestWithBody(
      method = PUT,
      endpoint = "/candidate-matching/job/2a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      body = sainsburysJobBody,
      expectedStatus = CREATED,
    )

    assertRequestWithBody(
      method = PUT,
      endpoint = "/candidate-matching/job/2a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      body = tescoJobBody,
      expectedStatus = OK,
    )
    assertResponse(
      endpoint = "/candidate-matching/job/2a553b0e-9d0b-46b2-bd78-3fa24b7232da",
      expectedStatus = OK,
      expectedResponse = tescoJobBody,
    )
  }

  @Test
  fun `retrieve an existing job`() {
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

  val sainsburysJobBody = """
        {
          "salaryPeriodName":  "PER_DAY",
          "workPatternName": "ANNUALISED_HOURS",
          "hoursName": "FULL_TIME",
          "employerId": "1a553b0e-9d0b-46b2-bd78-3fa24b7232da",
          "sectorName": "Food Industry",
          "additionalSalaryInformation": "be proactive and intresting",
          "desirableJobCriteria": "be a good conversationist",
          "essentialJobCriteria": null,
          "closingDate": "2024-06-18T11:45:25.619164",
          "howToApply": null,
          "jobTitle": "jobtitle1",
          "createdBy": null,
          "createdDateTime": null,
          "postingDate": null,
          "deletedBy": null,
          "deletedDateTime": null,
          "modifiedBy": null,
          "modifiedDateTime": null,
          "nationalMinimumWage": null,
          "postCode": null,
          "ringFencedJob": null,
          "rollingJobOppurtunity": null,
          "activeJob": null,
          "deletedJob": null,
          "salaryFrom": null,
          "salaryTo": null,
          "typeOfWork": "CONSTRUCTION"
        }
  """.trimIndent()

  val tescoJobBody = """
        {
          "salaryPeriodName":  "PER_DAY",
          "workPatternName": "ANNUALISED_HOURS",
          "hoursName": "FULL_TIME",
          "employerId": "1a553b0e-9d0b-46b2-bd78-3fa24b7232da",
          "sectorName": "Kids Industry",
          "additionalSalaryInformation": "be proactive and intresting",
          "desirableJobCriteria": "be a good conversationist",
          "essentialJobCriteria": null,
          "closingDate": "2024-06-18T11:45:25.619164",
          "howToApply": null,
          "jobTitle": "jobtitle1",
          "createdBy": null,
          "createdDateTime": null,
          "postingDate": null,
          "deletedBy": null,
          "deletedDateTime": null,
          "modifiedBy": null,
          "modifiedDateTime": null,
          "nationalMinimumWage": null,
          "postCode": null,
          "ringFencedJob": null,
          "rollingJobOppurtunity": null,
          "activeJob": null,
          "deletedJob": null,
          "salaryFrom": null,
          "salaryTo": null,
          "typeOfWork": "CONSTRUCTION"
        }
  """.trimIndent()
}
