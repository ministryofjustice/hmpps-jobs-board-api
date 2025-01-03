package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAmazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToTescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.knownApplicant
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.requestBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler

class ApplicationsPutShould : ApplicationsTestCase() {
  private val jobApplicant = knownApplicant

  @Test
  fun `not create application, when job is not found`() {
    applicationToAmazonForkliftOperator.let {
      assertAddApplicationFailedAsBadRequest(it, "Job not found: jobId=${it.job.id}")
    }
  }

  @Test
  fun `not create application, when prison number is empty`() {
    ApplicationMother.builder().from(applicationToAmazonForkliftOperator).apply {
      prisonNumber = ""
    }.build().let {
      assertAddApplicationFailedAsBadRequest(it, "prisonNumber cannot be empty")
    }
  }

  @Test
  fun `not create application, when prison number is not valid`() {
    ApplicationMother.builder().from(applicationToAmazonForkliftOperator).apply {
      prisonNumber = "A1234BCX"
    }.build().let {
      assertAddApplicationFailedAsBadRequest(it, "prisonNumber is too long")
    }
  }

  @Nested
  @DisplayName("Given a job to apply")
  inner class GiveAJobToApply {
    private val job = amazonForkliftOperator
    private val application = applicationToAmazonForkliftOperator

    @BeforeEach
    fun setUp() {
      givenJobsAreCreated(job)
    }

    @Test
    fun `create an application`() {
      assertAddApplicationIsCreated(application)
    }

    @Nested
    @DisplayName("And an application has been made")
    inner class AndAnApplicationMade {
      @BeforeEach
      fun setUp() {
        assertAddApplicationIsCreated(application)
      }

      @Test
      fun `update an application`() {
        val updatedApplication = ApplicationMother.builder().from(application).apply {
          this.additionalInformation = "PEL A: The candidate has been selected for interview in coming weeks (yet to book)."
          this.status = "SELECTED_FOR_INTERVIEW"
        }.build()
        assertUpdateApplicationIsOk(updatedApplication)
      }

      @Transactional(propagation = Propagation.NOT_SUPPORTED)
      @Test
      fun `update an application, that is retained after the job updated`() {
        val job = JobMother.builder().from(job).apply {
          additionalSalaryInformation = "updated info about salary: ... "
        }.build()
        assertUpdateJobIsOk(job.id.id, job.requestBody)

        val updatedApplication = ApplicationMother.builder().from(application).apply {
          this.status = ApplicationStatus.INTERVIEW_BOOKED.name
        }.build()
        assertUpdateApplicationIsOk(updatedApplication)
      }
    }
  }

  @Nested
  @DisplayName("Given a job that has been archived, for the given prisoner")
  inner class GiveAnArchivedJob {
    private val job = tescoWarehouseHandler
    private val application = applicationToTescoWarehouseHandler

    @BeforeEach
    fun setUp() {
      givenThreeJobsAreCreated()
      assertAddArchived(jobId = job.id.id, prisonNumber = jobApplicant.prisonNumber)
    }

    @Test
    fun `not create application to a job archived by the prisoner`() {
      assertAddApplicationFailedAsBadRequest(
        application,
        "Job has been archived for the prisoner: jobId=${job.id}, prisonNumber=${application.prisonNumber}",
      )
    }
  }
}
