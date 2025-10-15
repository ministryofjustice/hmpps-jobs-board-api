package uk.gov.justice.digital.hmpps.jobsboard.api.controller.sar

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.knownApplicant
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationsTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO

class SubjectAccessRequestGetShould : ApplicationsTestCase() {

  @Nested
  @DisplayName("/subject-access-request")
  inner class SubjectAccessRequestEndpoint {
    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}").andExpect { status { isUnauthorized() } }
      }

      @Test
      fun `access forbidden when no role`() {
        val headers = setAuthorisation()

        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { (name, values) ->
              values.forEach { value ->
                header(name, value)
              }
            }
          }
        }.andExpect { status { isForbidden() } }
      }

      @Test
      fun `access forbidden with wrong role`() {
        val headers = setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDIT"))

        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { (name, values) ->
              values.forEach { value ->
                header(name, value)
              }
            }
          }
        }.andExpect { status { isForbidden() } }
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `should return 204 if no prisoner data`() {
        val headers = setAuthorisation(roles = listOf("ROLE_SAR_DATA_ACCESS"))

        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { (name, values) ->
              values.forEach { value ->
                header(name, value)
              }
            }
          }
        }.andExpect {
          status { isNoContent() }
        }
      }

      @Test
      fun `should return data if prisoner exists`() {
        givenMoreApplicationsFromMultiplePrisons()

        val headers = setAuthorisation(roles = listOf("ROLE_SAR_DATA_ACCESS"))

        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { (name, values) ->
              values.forEach { value ->
                header(name, value)
              }
            }
          }
        }.andExpect {
          status { isOk() }
          content {
            contentType(APPLICATION_JSON)
            content {
              jsonPath("$.attachments", nullValue())
              jsonPath("$.content.applications[0].prisonNumber", equalTo(knownApplicant.prisonNumber))
              jsonPath("$.content.expressionsOfInterest", equalTo(emptyList<ExpressionOfInterestDTO>()))
              jsonPath("$.content.archivedJobs", equalTo(emptyList<ArchivedDTO>()))
            }
          }
        }
      }

      @Test
      fun `should return 209 if given a crn`() {
        val headers = setAuthorisation(roles = listOf("ROLE_SAR_DATA_ACCESS"))

        mockMvc.get("/subject-access-request?crn=A111111") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { (name, values) ->
              values.forEach { value ->
                header(name, value)
              }
            }
          }
        }.andExpect {
          status { isEqualTo(209) }
        }
      }
    }
  }
}
