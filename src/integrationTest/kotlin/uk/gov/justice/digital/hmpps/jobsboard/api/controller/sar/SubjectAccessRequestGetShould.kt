package uk.gov.justice.digital.hmpps.jobsboard.api.controller.sar

import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure.ApplicationAuditCleaner
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.knownApplicant
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationsTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SubjectAccessRequestGetShould : ApplicationsTestCase() {
  @Autowired
  protected lateinit var applicationAuditCleaner: ApplicationAuditCleaner

  @BeforeEach
  override fun setup() {
    super.setup()
    applicationAuditCleaner.deleteAllRevisions()
  }

  @Nested
  @DisplayName("/subject-access-request")
  inner class SubjectAccessRequestEndpoint {
    init {
      currentUser = "USER1_GEN"
    }

    @Nested
    inner class Security {
      @Test
      fun `access forbidden when no authority`() {
        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}").andExpect { status { isUnauthorized() } }
      }

      @Test
      fun `access forbidden when no role`() {
        val headers = httpHeaders()

        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { name, values ->
              values.forEach { value ->
                header(name, value)
              }
            }
          }
        }.andExpect { status { isForbidden() } }
      }

      @Test
      fun `access forbidden with wrong role`() {
        val headers = httpHeaders(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDIT"))

        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { name, values ->
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
      private val headers = httpHeaders(roles = listOf("ROLE_SAR_DATA_ACCESS"))

      @Test
      fun `should return 204 if no prisoner data`() {
        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { name, values ->
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
        assertAddArchived(jobId = amazonForkliftOperator.id.id, prisonNumber = knownApplicant.prisonNumber)

        mockMvc.get("/subject-access-request?prn=${knownApplicant.prisonNumber}") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { name, values ->
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
              jsonPath("$.content.applications[0].createdBy", equalTo(currentUser))
              jsonPath("$.content.applications[0].lastModifiedBy", equalTo(currentUser))
              jsonPath("$.content.applications[0].histories[0].modifiedBy", equalTo(currentUser))
              jsonPath("$.content.expressionsOfInterest", equalTo(emptyList<ExpressionOfInterestDTO>()))
              jsonPath("$.content.archivedJobs[0].createdBy", equalTo(currentUser))
            }
          }
        }
      }

      @Test
      fun `should return 209 if given a crn`() {
        mockMvc.get("/subject-access-request?crn=A111111") {
          contentType = APPLICATION_JSON
          accept = APPLICATION_JSON
          headers {
            headers.forEach { name, values ->
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
