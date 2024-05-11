package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobEmployer
import java.time.Instant
import java.time.ZoneOffset

@Service
class OutboundEventsService(
  var outboundEventsPublisher: OutboundEventsPublisher?,
) {

  fun createAndPublishEventMessage(jobEmployer: JobEmployer, eventType: EventType) {
    val outboundEvent = jobEmployer.employerName?.let {
      createValidJobsBoardEvent(
        it,
        eventType,
        jobEmployer.modifiedDateTime?.toInstant(
          ZoneOffset.UTC,
        )!!,
      )
    }
    outboundEvent?.let { outboundEventsPublisher?.publishToTopic(it) }
  }
  fun createValidJobsBoardEvent(
    employerName: String,
    eventType: EventType,
    instant: Instant,
  ): OutboundEvent =
    OutboundEvent(
      eventType = eventType,
      personReference = EmployerReference(listOf(Identifier("NOMS", employerName))),
      additionalInformation = AdditionalInformation(
        reference = employerName,
        Id = employerName,
        userId = CapturedSpringConfigValues.getDPSPrincipal().name,
        userDisplayName = CapturedSpringConfigValues.getDPSPrincipal().displayName,
      ),
      occurredAt = instant,
      1,
    )
}
