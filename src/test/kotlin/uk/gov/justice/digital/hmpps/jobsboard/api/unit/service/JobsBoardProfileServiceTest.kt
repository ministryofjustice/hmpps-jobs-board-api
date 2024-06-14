package uk.gov.justice.digital.hmpps.jobsboard.api.unit.service

import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.mock
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobsBoardProfileRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.service.JobsBoardProfileService
import uk.gov.justice.digital.hmpps.jobsboard.api.telemetry.TelemetryService

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

//  @Disabled("Empty test")
//  @Test
//  fun `makes a call to the repository to save the Job profile`() {
//  }
}
