package uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class EntityId(val id: String) : Serializable {
  constructor() : this(UUID.randomUUID().toString())

  init {
    require(id.isNotEmpty()) { "EntityId cannot be empty" }
    require(!id.equals("00000000-0000-0000-0000-00000")) { "EntityId cannot be null: {$id}" }
    require(isValidUUID(id)) { "Invalid UUID format: {$id}" }
  }

  override fun toString(): String = id

  private fun isValidUUID(uuid: String): Boolean {
    return try {
      id == UUID.fromString(uuid).toString()
    } catch (error: IllegalArgumentException) {
      false
    }
  }
}
