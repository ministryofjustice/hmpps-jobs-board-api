package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JobPrisonerIdShould {
  // This is UUID v4, not yet v7
  private val validJobId = "b67d9daf-fb7e-462b-9baf-dd4c8f62a3a7"

  @Test
  fun `create with valid Job ID and prisoner's prisonNumber`() {
    val jobId = validJobId
    val prisonNumber = "A1234BC"
    val id = makeId(jobId, prisonNumber)

    assertEquals(jobId, id.jobId.id)
    assertEquals(prisonNumber, id.prisonNumber)
  }

  @Test
  fun `throw exception with invalid Job ID`() {
    val invalidJobId = "b67d9daf-fb7e-462b-9baf-dd4c"
    val prisonNumber = "A1234BC"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(invalidJobId, prisonNumber) }

    assertEquals("Invalid UUID format: {$invalidJobId}", exception.message)
  }

  @Test
  fun `throw exception with empty Job ID`() {
    val emptyJobId = ""
    val prisonNumber = "A1234BC"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(emptyJobId, prisonNumber) }

    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception with null Job ID`() {
    val nullJobId = "00000000-0000-0000-0000-00000"
    val prisonNumber = "A1234BC"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(nullJobId, prisonNumber) }
    assertEquals("EntityId cannot be null: {$nullJobId}", exception.message)
  }

  @Test
  fun `throw exception with lengthy prisonNumber`() {
    val jobId = validJobId
    val prisonNumber = "A1234BCZ"
    val exception = assertFailsWith<IllegalArgumentException> { makeId(jobId, prisonNumber) }
    assertEquals("prisonNumber is too long", exception.message)
  }

  @Test
  fun `throw exception with empty prisoner's prisonNumber`() {
    val jobId = validJobId
    val prisonNumber = ""
    val exception = assertFailsWith<IllegalArgumentException> { makeId(jobId, prisonNumber) }
    assertEquals("prisonNumber cannot be empty", exception.message)
  }

  private fun makeId(jobId: String, prisonNumber: String): JobPrisonerId = JobPrisonerId(EntityId(jobId), prisonNumber)
}
