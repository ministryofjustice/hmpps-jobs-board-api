package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository.JobsBoardProfileRepository
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.telemetry.TelemetryService

@Service
class JobsBoardProfileService(
  private val jobsBoardProfileRepository: JobsBoardProfileRepository,
  private val outboundEventsService: OutboundEventsService,
  private val telemetryService: TelemetryService,
)
