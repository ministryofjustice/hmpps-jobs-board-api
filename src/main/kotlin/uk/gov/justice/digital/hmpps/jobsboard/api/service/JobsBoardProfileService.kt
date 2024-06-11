package uk.gov.justice.digital.hmpps.jobsboard.api.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobsBoardProfileRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.telemetry.TelemetryService

@Service
class JobsBoardProfileService(
  private val jobsBoardProfileRepository: JobsBoardProfileRepository,
  private val outboundEventsService: OutboundEventsService,
  private val telemetryService: TelemetryService,
)
