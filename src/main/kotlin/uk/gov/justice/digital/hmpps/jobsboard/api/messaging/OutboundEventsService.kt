package uk.gov.justice.digital.hmpps.jobsboard.api.messaging

import uk.gov.justice.digital.hmpps.jobsboard.api.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.JobsBoardProfile
import java.time.Instant
import java.time.ZoneOffset

// @Service
class OutboundEventsService(
  var outboundEventsPublisher: OutboundEventsPublisher?,
) {

  fun createAndPublishEventMessage(jobsBoardProfile: JobsBoardProfile, eventType: EventType) {
    val outboundEvent = createValidJobsBoardEvent(
      jobsBoardProfile.offenderId,
      jobsBoardProfile.prisonId,
      eventType,
      jobsBoardProfile.modifiedDateTime?.toInstant(
        ZoneOffset.UTC,
      )!!,
    )
    outboundEventsPublisher?.publishToTopic(outboundEvent)
  }
  fun createValidJobsBoardEvent(
    reference: String,
    prisonName: String?,
    eventType: EventType,
    instant: Instant,
  ): OutboundEvent =
    OutboundEvent(
      eventType = eventType,
      personReference = PersonReference(listOf(Identifier("NOMS", reference))),
      additionalInformation = AdditionalInformation(
        reference = reference,
        prisonId = prisonName,
        userId = CapturedSpringConfigValues.getDPSPrincipal().name,
        userDisplayName = CapturedSpringConfigValues.getDPSPrincipal().displayName,
      ),
      occurredAt = instant,
      version = 1,
    )
}
