package uk.gov.justice.digital.hmpps.jobsboard.api.controller.dashboard

import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure.ApplicationAuditCleaner
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationsTestCase
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

const val DASHBOARD_ENDPOINT = "/dashboard"

abstract class DashboardTestCase : ApplicationsTestCase() {
  @Autowired
  protected lateinit var applicationAuditCleaner: ApplicationAuditCleaner

  protected val currentTime: Instant get() = defaultCurrentTime
  protected val currentDate: LocalDate get() = LocalDate.ofInstant(currentTime, ZoneOffset.UTC)

  @BeforeEach
  internal fun setUpDashboardTestCase() {
    applicationAuditCleaner.deleteAllRevisions()
    whenever(dateTimeProvider.now).thenAnswer { Optional.of(currentTime) }
  }
}
