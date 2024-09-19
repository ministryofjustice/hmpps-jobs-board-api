package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT

class ArchivedDeleteShould : ArchivedTestCase() {

  @Test
  fun `delete Archived, when it exists`() {
    val ids = givenAJobIsCreatedWithArchived()
    val jobId = ids[0]
    val prisonNumber = ids[1]

    assertDeleteArchived(
      jobId = jobId,
      prisonNumber = prisonNumber,
      expectedStatus = NO_CONTENT,
    )
  }

  @Test
  fun `do NOT delete Archived, when it does NOT exist, and return error`() {
    val jobId = givenAJobIsCreatedWithArchived().first()

    assertDeleteArchived(
      jobId = jobId,
      prisonNumber = nonExistentPrisonNumber,
      expectedStatus = NOT_FOUND,
    )
  }

  @Test
  fun `do NOT delete Archived with non-existent job, and return error`() {
    assertDeleteArchived(
      jobId = randomUUID(),
      prisonNumber = randomPrisonNumber(),
      expectedStatus = BAD_REQUEST,
    )
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
