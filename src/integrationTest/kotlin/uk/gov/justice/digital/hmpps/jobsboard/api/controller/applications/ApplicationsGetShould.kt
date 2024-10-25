package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.knownApplicant
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonMDI

class ApplicationsGetShould : ApplicationsTestCase() {
  private val defaultPageSize = 20
  private val prisonId = knownApplicant.prisonId

  @Test
  fun `return error when missing prisonId`() {
    assertGetApplicationsFailedAsBadRequest(
      expectedErrorMessage = "Required request parameter 'prisonId' for method parameter type String is not present",
    )
  }

  @Nested
  @DisplayName("Given no application, with the given prisonId")
  inner class GivenNoApplications {
    @Test
    fun `return a default paginated empty applications list`() {
      assertGetApplicationsIsOk(
        parameters = "prisonId=$prisonId",
        expectedResponse = expectedResponseListOf(defaultPageSize, 0),
      )
    }
  }

  @Nested
  @DisplayName("Given some applications, with the given prisonId")
  inner class GivenSomeApplications {
    @BeforeEach
    fun setUp() = givenMoreApplicationsFromMultiplePrisons()

    @Test
    fun `return a default paginated applications list, for given prison `() {
      val prisonId = prisonMDI
      val expectedPageSize = defaultPageSize
      val expectedPage = 0

      assertGetApplicationsIsOk(
        parameters = "prisonId=$prisonId",
        expectedResponse = expectedResponseListOf(
          size = expectedPageSize,
          page = expectedPage,
          elements = applicationsFromPrisonMDI.map { it.searchResponseBody }.toTypedArray(),
        ),
      )
    }
  }
}
