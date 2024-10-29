package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.requestBody

class ArchivedDeleteShould : ArchivedTestCase() {

  @Nested
  @DisplayName("Given a job has been created")
  inner class GivenAJobCreated {

    @Nested
    @DisplayName("And Job has been archived, for given prisoner")
    inner class AndJobArchived {
      private lateinit var jobId: String
      private lateinit var prisonNumber: String
      private val expectedJob = amazonForkliftOperator

      @BeforeEach
      fun setUp() {
        givenAJobIsCreatedWithArchived().let { ids ->
          jobId = ids[0]
          prisonNumber = ids[1]
        }
      }

      @Test
      fun `delete Archived, when it exists`() {
        assertDeleteArchived(
          jobId = jobId,
          prisonNumber = prisonNumber,
          expectedStatus = NO_CONTENT,
        )
      }

      @Test
      fun `do NOT delete Archived, when it does NOT exist, and return error`() {
        assertDeleteArchived(
          jobId = jobId,
          prisonNumber = nonExistentPrisonNumber,
          expectedStatus = NOT_FOUND,
        )
      }

      @Transactional(propagation = Propagation.NOT_SUPPORTED)
      @Test
      fun `delete Archived, that is retained after the job updated`() {
        val job = JobMother.builder().from(expectedJob).apply {
          additionalSalaryInformation = "updated info about salary: ... "
        }.build()
        assertUpdateJobIsOk(jobId, job.requestBody)

        assertDeleteArchived(
          jobId = jobId,
          prisonNumber = prisonNumber,
          expectedStatus = NO_CONTENT,
        )
      }
    }
  }

  @Nested
  @DisplayName("Given no job")
  inner class GivenNoJob {
    @Test
    fun `do NOT delete Archived with non-existent job, and return error`() {
      assertDeleteArchived(
        jobId = randomUUID(),
        prisonNumber = randomPrisonNumber(),
        expectedStatus = BAD_REQUEST,
      )
    }
  }

  @Test
  fun `do NOT delete Archived with invalid Job ID, and return error`() {
    val invalidJobID = invalidUUID
    assertAddArchivedThrowsValidationOrIllegalArgumentError(
      jobId = invalidJobID,
      expectedResponse = """
        {
          "status": 400,
          "errorCode": null,
          "userMessage": "Validation failure: create.jobId: Invalid UUID format",
          "developerMessage": "create.jobId: Invalid UUID format",
          "moreInfo": null
        }
      """.trimIndent(),
    )
  }

  @Test
  fun `do NOT delete Archived without prisoner's prisonNumber, and return error`() {
    assertDeleteArchivedRepliesNotFoundError(
      jobId = randomUUID(),
    )
  }

  private fun givenAJobIsCreatedWithArchived(): Array<String> {
    val jobId = obtainJobIdGivenAJobIsJustCreated()
    assertAddArchived(jobId = jobId, prisonNumber = prisonNumber)
    return arrayOf(jobId, prisonNumber)
  }
}
