package uk.gov.justice.digital.hmpps.jobsboard.api.unit.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.justice.digital.hmpps.jobsboard.api.assemblers.EmployerJobModelAssembler
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.service.PrisonLeaversJobService
import uk.gov.justice.digital.hmpps.jobsboard.api.telemetry.TelemetryService

class PrisonLeaversJobServiceTest {
  private val prisonLeaversJobRepository: PrisonLeaversJobRepository = mock()
  private val outboundEventsService: OutboundEventsService = mock()
  private val telemetryService: TelemetryService = mock()
  private val employerJobModelAssembler: EmployerJobModelAssembler = mock()

  private lateinit var prisonLeaversJobService: PrisonLeaversJobService

  @BeforeEach
  fun beforeEach() {
    prisonLeaversJobService = PrisonLeaversJobService(
      prisonLeaversJobRepository,
      outboundEventsService,
      telemetryService,
      employerJobModelAssembler,

    )
  }

  @Test
  fun `makes a call to the repository to save the Job profile`() {
  }
}
