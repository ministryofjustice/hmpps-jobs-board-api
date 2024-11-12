package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAbcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAmazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToTescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.historyResponseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.knownApplicant
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.util.*

class ApplicationHistoriesGetShould : ApplicationHistoriesTestCase() {

  @Test
  fun `return an error, when missing prisonNumber and jobId`() {
    assertGetApplicationHistoriesReturnsBadRequestError()
  }

  @Test
  fun `return an error, when missing jobId`() {
    assertGetApplicationHistoriesReturnsBadRequestError("prisonNumber=A1234BC")
  }

  @Test
  fun `return an error, when missing prisonNumber`() {
    assertGetApplicationHistoriesReturnsBadRequestError("jobId=34ae887f-ceee-444c-8101-1e9bccc3c773")
  }

  @Test
  fun `return an empty list, when there is no job application`() {
    assertGetApplicationHistoriesIsOk(
      parameters = "prisonNumber=A1234BC&jobId=34ae887f-ceee-444c-8101-1e9bccc3c773",
      expectedResponse = "[]",
    )
  }

  @Nested
  @DisplayName("Given some applications have been made")
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  inner class GivenApplicationsMade {
    private val prisonNumber = knownApplicant.prisonNumber

    private val applicationToJobOfInterest = applicationToAmazonForkliftOperator
      .copy(id = EntityId(UUID.randomUUID().toString()))
    private val applicationToJobArchived = applicationToTescoWarehouseHandler
      .copy(id = EntityId(UUID.randomUUID().toString()))
    private val jobOfInterest = applicationToJobOfInterest.job
    private val archivedJob = applicationToJobArchived.job

    private val expectedApplication = applicationToAbcConstructionApprentice
      .copy(id = EntityId(UUID.randomUUID().toString()))
    private val expectedJob = expectedApplication.job

    @BeforeEach
    fun setUp() {
      givenApplicationsAreCreated(applicationToJobOfInterest, applicationToJobArchived, expectedApplication)
    }

    @Test
    fun `return histories of application`() {
      assertGetApplicationHistoriesIsOk(prisonNumber, expectedJob.id.id, expectedApplication)
    }

    @Test
    fun `return histories of application, that has been updated`() {
      val updatedApplication = builder().from(expectedApplication).apply {
        status = ApplicationStatus.JOB_OFFER.toString()
      }.build()
      assertUpdateApplicationIsOk(updatedApplication)

      assertGetApplicationHistoriesIsOk(prisonNumber, expectedJob.id.id, expectedApplication, updatedApplication)
    }

    @Nested
    @DisplayName("And an application has been made to a job of interest")
    inner class AndWithJobOfInterest {
      private val expectedApplication = applicationToJobOfInterest
      private val expectedJob = jobOfInterest

      @BeforeEach
      fun setUp() {
        assertAddExpressionOfInterest(
          jobId = expectedJob.id.id,
          prisonNumber = prisonNumber,
          expectedStatus = CREATED,
        )
      }

      @Test
      fun `return histories of application, to a job of interest`() {
        assertGetApplicationHistoriesIsOk(prisonNumber, expectedJob.id.id, expectedApplication)
      }
    }

    @Nested
    @DisplayName("And an application has been made to a job archived for the prisoner afterward")
    inner class AndWithJobArchived {
      private val expectedApplication = applicationToJobArchived
      private val expectedJob = archivedJob

      @BeforeEach
      fun setUp() {
        assertAddArchived(
          jobId = expectedJob.id.id,
          prisonNumber = prisonNumber,
          expectedStatus = CREATED,
        )
      }

      @Test
      fun `return histories of application, to a job archived for the prisoner afterward`() {
        assertGetApplicationHistoriesIsOk(prisonNumber, expectedJob.id.id, expectedApplication)
      }
    }
  }

  private fun assertGetApplicationHistoriesIsOk(
    prisonNumber: String,
    jobId: String,
    vararg expectedApplicationHistories: Application,
  ) = assertGetApplicationHistoriesIsOk(prisonNumber, jobId, listOf(*expectedApplicationHistories))

  private fun assertGetApplicationHistoriesIsOk(
    prisonNumber: String,
    jobId: String,
    expectedApplicationHistories: List<Application>? = null,
  ) {
    val expectedResponse = expectedApplicationHistories?.let {
      it.map { it.historyResponseBody }.joinToString(separator = ",", prefix = "[", postfix = "]")
    }
    expectedResponseListOf()
    assertGetApplicationHistoriesIsOk(
      parameters = "prisonNumber=$prisonNumber&jobId=$jobId",
      expectedResponse = expectedResponse,
    )
  }
}
