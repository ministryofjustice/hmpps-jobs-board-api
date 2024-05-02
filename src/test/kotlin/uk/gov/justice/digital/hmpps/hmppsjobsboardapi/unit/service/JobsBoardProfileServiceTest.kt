package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.unit.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository.JobsBoardProfileRepository
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.service.JobsBoardProfileService
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.telemetry.TelemetryService

class JobsBoardProfileServiceTest {
  private val jobsBoardProfileRepository: JobsBoardProfileRepository = mock()
  private val outboundEventsService: OutboundEventsService = mock()
  private val telemetryService: TelemetryService = mock()

  private lateinit var profileService: JobsBoardProfileService

  @BeforeEach
  fun beforeEach() {
    profileService = JobsBoardProfileService(
      jobsBoardProfileRepository,
      outboundEventsService,
      telemetryService,
    )
  }

  @Test
  fun `makes a call to the repository to save the Job profile`() {
  }
}
