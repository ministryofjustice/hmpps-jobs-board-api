package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExpressionOfInterestIdShould {
  // This is UUID v4, not yet v7
  private val validJobId = "b67d9daf-fb7e-462b-9baf-dd4c8f62a3a7"

  @Test
  fun `create with valid Job-ID and prisoner's Prison-Number`() {
    val jobId = validJobId
    val prisonerPrisonNumber = "A1234BC"
    val id = makeId(jobId, prisonerPrisonNumber)

    assertEquals(jobId, id.jobId.id)
    assertEquals(prisonerPrisonNumber, id.prisonerPrisonNumber)
  }

  @Test
  fun `throw exception with invalid Job-ID`() {
    val invalidJobId = "b67d9daf-fb7e-462b-9baf-dd4c"
    val prisonerPrisonNumber = "A1234BC"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(invalidJobId, prisonerPrisonNumber) }

    assertEquals("Invalid UUID format: {$invalidJobId}", exception.message)
  }

  @Test
  fun `throw exception with empty Job-ID`() {
    val emptyJobId = ""
    val prisonerPrisonNumber = "A1234BC"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(emptyJobId, prisonerPrisonNumber) }

    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception with null Job-ID`() {
    val nullJobId = "00000000-0000-0000-0000-00000"
    val prisonerPrisonNumber = "A1234BC"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(nullJobId, prisonerPrisonNumber) }
    assertEquals("EntityId cannot be null: {$nullJobId}", exception.message)
  }

  @Test
  fun `throw exception with lengthy Prison-Number`() {
    val jobId = validJobId
    val prisonerPrisonNumber = "A1234BCZ"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(jobId, prisonerPrisonNumber) }
    assertEquals("prisonerPrisonNumber is too long", exception.message)
  }

  @Test
  fun `throw exception with empty prisoner's Prison-Number`() {
    val jobId = validJobId
    val prisonerPrisonNumber = ""
    val exception = assertFailsWith<IllegalArgumentException> { makeId(jobId, prisonerPrisonNumber) }
    assertEquals("prisonerPrisonNumber cannot be empty", exception.message)
  }

  private fun makeId(jobId: String, prisonerPrisonNumber: String): ExpressionOfInterestId =
    ExpressionOfInterestId(EntityId(jobId), prisonerPrisonNumber)
}
