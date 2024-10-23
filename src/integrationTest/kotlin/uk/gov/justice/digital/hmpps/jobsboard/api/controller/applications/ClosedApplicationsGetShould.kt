package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAbcConstructionApprentice

class ClosedApplicationsGetShould : ClosedApplicationsTestCase() {
  private val knownApplicant = ApplicationMother.KnownApplicant

  @Test
  fun `return an error, when missing prisonNumber`() {
    assertGetClosedApplicationsReturnsBadRequestError()
  }

  @Test
  fun `return an empty list, for unknown prisoner`() {
    assertGetClosedApplicationsIsOk("prisonNumber=X9876YZ", expectedResponseListOf())
  }

  @Nested
  @DisplayName("Given no application has been made, for the given prisoner")
  inner class GivenNoApplication {
    @Test
    fun `return a default paginated list of closed applications`() {
      assertGetClosedApplicationsIsOk("prisonNumber=${knownApplicant.prisonNumber}", expectedResponseListOf())
    }
  }

  @Nested
  @DisplayName("Given some applications have been made, for the given prisoner")
  inner class GivenSomeApplications {
    @BeforeEach
    fun setUp() {
      givenThreeApplicationsAreCreated()
    }

    @Test
    fun `return a paginated list of closed applications`() {
      assertGetClosedApplicationsIsOk(
        parameters = "prisonNumber=${knownApplicant.prisonNumber}",
        expectedResponse = expectedResponseListOf(
          size = 10,
          page = 0,
          totalElements = 1,
          applicationToAbcConstructionApprentice.responseBody,
        ),
      )
    }

    @Nested
    @DisplayName("And a custom pagination has been set")
    inner class CustomPagination {
      @Test
      fun `return a custom paginated list of closed applications`() {
        assertGetClosedApplicationsIsOk(
          parameters = "prisonNumber=${knownApplicant.prisonNumber}&page=0&size=1",
          expectedResponse = expectedResponseListOf(
            size = 1,
            page = 0,
            totalElements = 1,
            applicationToAbcConstructionApprentice.responseBody,
          ),
        )
      }
    }
  }
}
