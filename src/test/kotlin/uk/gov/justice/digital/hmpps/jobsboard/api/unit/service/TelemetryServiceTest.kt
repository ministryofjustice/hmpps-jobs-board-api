package uk.gov.justice.digital.hmpps.jobsboard.api.unit.service

import com.microsoft.applicationinsights.TelemetryClient
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.mock
import uk.gov.justice.digital.hmpps.jobsboard.api.telemetry.TelemetryService

class TelemetryServiceTest {
  private val telemetryClient: TelemetryClient = mock()

  private lateinit var telemetryService: TelemetryService

  @BeforeEach
  fun beforeEach() {
    telemetryService = TelemetryService(
      telemetryClient,
    )
  }

//  @Disabled("Empty test")
//  @Test
//  fun `makes a call to the telemetryclient when a job is created`() {
//  }

//  @Disabled("Empty test")
//  @Test
//  fun `makes a call to the telemetryclient when a job is updated`() {
//  }
}
