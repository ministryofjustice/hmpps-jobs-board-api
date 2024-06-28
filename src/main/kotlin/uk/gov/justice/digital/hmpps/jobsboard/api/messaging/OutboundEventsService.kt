package uk.gov.justice.digital.hmpps.jobsboard.api.messaging

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.JobEmployerDTO
import java.time.Instant
import java.time.ZoneOffset

@Service
class OutboundEventsService(
  var outboundEventsPublisher: OutboundEventsPublisher?,
) {

  fun createAndPublishEventMessage(jobEmployerDTO: JobEmployerDTO, eventType: EventType) {
    val outboundEvent = createValidJobsBoardEvent(
      jobEmployerDTO.employerBio!!,
      jobEmployerDTO.employerName,
      eventType,
      jobEmployerDTO.modifiedDateTime?.toInstant(
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
