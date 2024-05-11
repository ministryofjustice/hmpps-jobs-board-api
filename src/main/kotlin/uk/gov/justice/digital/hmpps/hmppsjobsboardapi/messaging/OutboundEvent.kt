package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging

import com.fasterxml.jackson.annotation.JsonValue
import java.time.Instant

data class OutboundEvent(
  val eventType: EventType,
  val personReference: EmployerReference,
  val additionalInformation: AdditionalInformation,
  val occurredAt: Instant,
  val version: Int,
) {
  fun reference() = additionalInformation.reference
  fun prisonNumber(): String = personReference.identifiers.first { it.type == "NOMS" }.value
  fun prisonId() = additionalInformation.reference
  fun userId() = additionalInformation.userId
  fun userDisplayName() = additionalInformation.userDisplayName
}

enum class EventType(@JsonValue val eventType: String) {
  JOB_BOARD_CREATED("job-board.created"),
  JOB_BOARD_UPDATED("job-board.updated"),
}

data class AdditionalInformation(
  val reference: String,
  val Id: String?,
  val userId: String,
  val userDisplayName: String?,
)

data class EmployerReference(val identifiers: List<Identifier>)

data class Identifier(val type: String, val value: String)
