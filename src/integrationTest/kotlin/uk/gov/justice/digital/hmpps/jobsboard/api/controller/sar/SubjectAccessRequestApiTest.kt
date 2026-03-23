package uk.gov.justice.digital.hmpps.jobsboard.api.controller.sar

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure.ApplicationAuditCleaner
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.knownApplicant
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationsTestCase
import uk.gov.justice.digital.hmpps.subjectaccessrequest.SarApiDataTest
import uk.gov.justice.digital.hmpps.subjectaccessrequest.SarApiTestBase
import uk.gov.justice.digital.hmpps.subjectaccessrequest.SarIntegrationTestHelper
import uk.gov.justice.digital.hmpps.subjectaccessrequest.SarReportTest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * This SAR test check APIs: data and report
 */
class SubjectAccessRequestApiTest {

  /**
   * Check API data returned from SAR endpoint
   */
  @Nested
  @DisplayName("API data test")
  inner class ApiDataTest :
    TestCase(),
    SarApiDataTest

  /**
   * Check report content rendered
   */
  @Nested
  @DisplayName("Report test")
  inner class ReportTest :
    TestCase(),
    SarReportTest

  /**
   * Check report timestamps in DST (daylight saving time)
   */
  @Nested
  @DisplayName("Report test with DST (daylight saving time)")
  inner class ReportTestDst : TestCase() {

    /**
     * taken from `SarReportTest` and modified for DST
     *
     * UK DST in 2025:  Sun, 30 Mar 2025 (1am->2am) – Sun, 26 Oct 2025 (2am -> 1am)
     */
    @ParameterizedTest
    @CsvSource(
      """
        2025-03-30T01:00:00Z, sar-generated-report-dst.html
        2025-10-26T01:00:00Z, sar-generated-report-after-dst.html""",
    )
    fun `SAR report should render as expected with daylight saving time (DST)`(
      currentTime: Instant,
      reportFileName: String,
    ) {
      sarCurrentTime = currentTime
      val expectedResult by lazy { getSarHelper().getResourceAsString("/sar/$reportFileName") }

      setupTestData()
      getSarHelper().stubFindPrisonNameWith("Moorland (HMP & YOI)")
      getSarHelper().stubFindUserLastNameWith("Johnson")
      getSarHelper().stubFindLocationNameByNomisIdWith("PROPERTY BOX 1")
      getSarHelper().stubFindLocationNameByDpsIdWith("PROPERTY BOX 2")
      val dataResponse =
        getSarHelper().requestSarData(getPrn(), getCrn(), getFromDate(), getToDate(), getWebTestClientInstance())
      val templateResponse = getSarHelper().requestSarTemplate(getWebTestClientInstance())

      val renderResult = getSarHelper().renderServiceReport(
        data = dataResponse.content,
        templateVersion = "1.0",
        template = templateResponse,
      )
      if (System.getenv("SAR_GENERATE_ACTUAL").toBoolean()) {
        getSarHelper().saveContentToFile(renderResult, "$reportFileName.log")
      } else {
        getSarHelper().assertHtmlEquals(renderResult, expectedResult)
      }
    }
  }

  abstract class TestCase : SubjectAccessRequestApiTestCase() {
    @Autowired
    protected lateinit var webTestClient: WebTestClient

    private val sarPrisonNumber = knownApplicant.prisonNumber

    override fun getSarHelper(): SarIntegrationTestHelper = sarIntegrationTestHelper
    override fun getWebTestClientInstance(): WebTestClient = webTestClient

    override fun setupTestData() {
      val applications = applicationsFromPrisonMDI

      // 1) `Applications`: application, histories
      givenApplicationsAreCreated(*applications.toTypedArray())
      // 2) `Expressions of interest`
      applications.first().apply { assertAddExpressionOfInterest(job.id.id, sarPrisonNumber) }
      // 3) `Archived jobs`
      applications.last().apply { assertAddArchived(job.id.id, sarPrisonNumber) }
    }

    override fun getPrn(): String? = sarPrisonNumber
  }
}

/**
 * Subject access request API test case
 *
 * 1) Disable Transaction for ENVERS to produce history records
 * 2) Set current time on a day with DST (daylight saving time); Use British Summer Time (BST) at tests
 *    ref: https://www.gov.uk/when-do-the-clocks-change
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
abstract class SubjectAccessRequestApiTestCase :
  ApplicationsTestCase(),
  SarApiTestBase {

  @Autowired
  protected lateinit var applicationAuditCleaner: ApplicationAuditCleaner

  protected var sarCurrentTime: Instant = defaultCurrentTime

  protected val sarTimezoneId: ZoneId = ZoneId.of("Europe/London")
  protected val sarCurrentTimeLocal: LocalDateTime get() = sarCurrentTime.atZone(sarTimezoneId).toLocalDateTime()

  override fun getCrn(): String? = null
  override fun getFromDate(): LocalDate? = sarCurrentTimeLocal.minusDays(1L).toLocalDate()
  override fun getToDate(): LocalDate? = sarCurrentTimeLocal.plusDays(1L).toLocalDate()

  @BeforeEach
  override fun setup() {
    super.setup()
    applicationAuditCleaner.deleteAllRevisions()
    whenever(dateTimeProvider.now).thenAnswer { Optional.of(sarCurrentTime) }
  }
}
