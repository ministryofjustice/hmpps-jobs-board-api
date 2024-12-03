package uk.gov.justice.digital.hmpps.jobsboard.api.controller.dashboard

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonABC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonXYZ

class MetricsLatestApplicationsStatusGetShould : MetricsLatestApplicationsStatusTestCase() {
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

    @Test
    fun `return error, when invalid reporting period has been specified`() {
      val dateFrom = "2024-02-01"
      val dateTo = "2024-01-31"
      assertGetMetricsReturnsBadRequestError(
        parameters = "prisonId=MDI&dateFrom=$dateFrom&dateTo=$dateTo",
        expectedResponse = expectedErrorMessageInvalidDatePeriod(dateFrom, dateTo),
      )
    }
  }

  @Nested
  @DisplayName("Given no application, with the given prisonId")
  inner class GivenNoApplications {
    private val prisonId = prisonMDI

    @Test
    fun `return empty count at metrics`() {
      val today = currentDate
      val tomorrow = today.plusDays(1)
      assertGetMetricsIsOk(prisonId, today, tomorrow, "[]")
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
    fun `return correct counts at metrics, given prison with applications from single applicant`() {
      val expectedResponse = mapOf(
        ApplicationStatus.APPLICATION_MADE to 2L,
      ).metricsResponses

      assertGetMetricsIsOk(prisonMDI, yesterday, tomorrow, expectedResponse)
    }

    @Test
    fun `return correct counts at metrics, given prison with applications from multiple applicants`() {
      val expectedResponse = mapOf(
        ApplicationStatus.APPLICATION_MADE to 4L,
        ApplicationStatus.INTERVIEW_BOOKED to 1,
        ApplicationStatus.SELECTED_FOR_INTERVIEW to 1,
      ).metricsResponses

      assertGetMetricsIsOk(prisonABC, yesterday, tomorrow, expectedResponse)
    }

    @Test
    fun `return empty count at metrics, that applications of other prison(s) will be excluded`() {
      assertGetMetricsIsOk(prisonXYZ, yesterday, tomorrow, "[]")
    }

    @Test
    fun `return empty count at metrics, that applications were created or updated BEFORE reporting period`() {
      assertGetMetricsIsOk(prisonMDI, yesterday, yesterday, "[]")
    }

    @Test
    fun `return empty count at metrics, that applications were created or updated AFTER reporting period`() {
      assertGetMetricsIsOk(prisonMDI, tomorrow, tomorrow, "[]")
    }
  }
}
