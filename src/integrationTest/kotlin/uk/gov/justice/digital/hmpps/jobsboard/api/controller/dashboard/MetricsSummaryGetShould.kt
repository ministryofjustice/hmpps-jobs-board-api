package uk.gov.justice.digital.hmpps.jobsboard.api.controller.dashboard

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonABC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonXYZ

class MetricsSummaryGetShould : MetricsSummaryTestCase() {
  private val expectedZeroesResponse = metricSummaryResponse(0, 0)

  @Nested
  @DisplayName("Given invalid request parameter(s)")
  inner class GivenInvalidRequestParameter {
    @Test
    fun `return error, when missing all parameters`() {
      assertGetMetricsReturnsBadRequestError()
    }

    @Test
    fun `return error, when missing parameter prisonId`() {
      assertGetMetricsReturnsBadRequestError(
        parameters = "dateFrom=$currentDate&dateTo=$currentDate",
        expectedResponse = expectedErrorMessageMissingParameter("prisonId"),
      )
    }

    @Test
    fun `return error, when missing parameter dateFrom`() {
      assertGetMetricsReturnsBadRequestError(
        parameters = "prisonId=MDI&dateTo=$currentDate",
        expectedResponse = expectedErrorMessageMissingDateParameter("dateFrom"),
      )
    }

    @Test
    fun `return error, when missing parameter dateTo`() {
      assertGetMetricsReturnsBadRequestError(
        parameters = "prisonId=MDI&dateFrom=$currentDate",
        expectedResponse = expectedErrorMessageMissingDateParameter("dateTo"),
      )
    }

    @Test
    fun `return error, when invalid format of date parameter has been specified`() {
      val invalidDate = "2099_12_31"
      assertGetMetricsReturnsBadRequestError(
        parameters = "prisonId=MDI&dateFrom=$invalidDate&dateTo=$invalidDate",
        expectedResponse = expectedErrorMessageParameterTypeMismatch("dateFrom", invalidDate),
      )
    }

    @Test
    fun `return error, when invalid value of date parameter has been specified`() {
      val invalidDate = "abc"
      assertGetMetricsReturnsBadRequestError(
        parameters = "prisonId=MDI&dateFrom=$invalidDate&dateTo=$invalidDate",
        expectedResponse = expectedErrorMessageParameterTypeMismatch("dateFrom", invalidDate),
      )
    }
  }

  @Nested
  @DisplayName("Given no application, with the given prisonId")
  inner class GivenNoApplications {
    private val prisonId = prisonMDI

    @Test
    fun `return zeroes at metrics summary`() {
      val today = currentDate
      val tomorrow = today.plusDays(1)
      assertGetMetricsIsOk(prisonId, today, tomorrow, expectedZeroesResponse)
    }
  }

  @Nested
  @DisplayName("Given some applications, with the given prisonId")
  inner class GivenSomeApplications {
    private val yesterday = currentDate.minusDays(1)
    private val today = currentDate
    private val tomorrow = today.plusDays(1)

    @BeforeEach
    fun setUp() = givenMoreApplicationsFromMultiplePrisons()

    @Test
    fun `return correct counts at metrics summary, given prison with applications from single applicant`() {
      assertGetMetricsIsOk(prisonMDI, yesterday, tomorrow, metricSummaryResponse(1, 3))
    }

    @Test
    fun `return correct counts at metrics summary, given prison with applications from multiple applicants`() {
      assertGetMetricsIsOk(prisonABC, yesterday, tomorrow, metricSummaryResponse(5, 3))
    }

    @Test
    fun `return zeroes at metrics summary, that applications of other prison(s) will be excluded`() {
      assertGetMetricsIsOk(prisonXYZ, yesterday, tomorrow, expectedZeroesResponse)
    }

    @Test
    fun `return zeroes at metrics summary, that applications were created or updated BEFORE reporting period`() {
      assertGetMetricsIsOk(prisonMDI, yesterday, yesterday, expectedZeroesResponse)
    }

    @Test
    fun `return zeroes at metrics summary, that applications were created or updated AFTER reporting period`() {
      assertGetMetricsIsOk(prisonMDI, tomorrow, tomorrow, expectedZeroesResponse)
    }
  }
}
