package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class EntityId(val id: String) : Serializable {
  constructor() : this(UUID.randomUUID().toString())

  init {
    require(isValidUUID(id)) { "Invalid UUID format: $id" }
  }

  override fun toString(): String = id

  private fun isValidUUID(uuidString: String): Boolean {
    return try {
      UUID.fromString(uuidString)
      true
    } catch (error: IllegalArgumentException) {
      false
    }
  }
}
