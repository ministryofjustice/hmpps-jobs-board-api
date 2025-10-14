package uk.gov.justice.digital.hmpps.jobsboard.api.controller.sar

import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.ApplicationTestCase

class SubjectAccessRequestGetShould : ApplicationTestCase() {

  @Test
  fun `return error when missing role`() {
    val headers: HttpHeaders = this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDIT"))

    val resultActions = mockMvc.get("/subject-access-request") {
      contentType = APPLICATION_JSON
      accept = APPLICATION_JSON
      headers {
        headers.forEach { (name, values) ->
          values.forEach { value ->
            header(name, value)
          }
        }
      }
    }

    resultActions.andExpect {
      status { isEqualTo(401) }
    }
  }
}

