package uk.gov.justice.digital.hmpps.jobsboard.api.unit.messaging

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import uk.gov.justice.digital.hmpps.jobsboard.api.config.DpsPrincipal
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsPublisher
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsService

@ExtendWith(MockitoExtension::class)
class OutboundEventsServiceTest {
  private var outboundEventsService: OutboundEventsService? = null

  @Mock
  private val outboundEventsPublisher: OutboundEventsPublisher? = null

  @Mock
  private val securityContext: SecurityContext? = null

  @Mock
  private val authentication: Authentication? = null

  private val dpsPrincipal: DpsPrincipal = DpsPrincipal("Sacintha", "Sacintha Raj")

  @BeforeEach
  fun beforeClass() {
    outboundEventsService = OutboundEventsService(outboundEventsPublisher!!)
  }

  @Test
  fun should_Publish_Event_ForJobCreationAndUpdate() {
  }
}
