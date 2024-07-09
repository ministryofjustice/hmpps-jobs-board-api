package uk.gov.justice.digital.hmpps.jobsboard.api.unit.entity

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EntityIdShould {

  @Test
  fun `create EntityId with valid UUID`() {
    val validUUID = "57cc41f1-6a1e-4793-8921-97b02016b290"
    val entityId = EntityId(validUUID)
    assertEquals(validUUID, entityId.id)
  }

  @Test
  fun `throw exception for invalid UUID`() {
    val invalidUUID = "invalid-uuid"
    val exception = assertFailsWith<IllegalArgumentException> {
      EntityId(invalidUUID)
    }
    assertEquals("Invalid UUID format: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception for empty UUID`() {
    val exception = assertFailsWith<IllegalArgumentException> {
      EntityId("")
    }
    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception for null UUID`() {
    val invalidUUID = "00000000-0000-0000-0000-00000"
    val exception = assertFailsWith<IllegalArgumentException> {
      EntityId(invalidUUID)
    }
    assertEquals("EntityId cannot be null: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception for incomplete UUID`() {
    val invalidUUID = "68bba8de-4542-4f4c-9a3d-0"
    val exception = assertFailsWith<IllegalArgumentException> {
      EntityId(invalidUUID)
    }
    assertEquals("Invalid UUID format: {$invalidUUID}", exception.message)
    println(exception.message)
  }
}
