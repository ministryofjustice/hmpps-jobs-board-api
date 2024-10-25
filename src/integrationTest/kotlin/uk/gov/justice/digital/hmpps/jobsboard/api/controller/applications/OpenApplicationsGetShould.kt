package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAmazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToTescoWarehouseHandler

class OpenApplicationsGetShould : OpenApplicationsTestCase() {
  private val knownApplicant = ApplicationMother.knownApplicant

  @Test
  fun `return an error, when missing prisonNumber`() {
    assertGetOpenApplicationsReturnsBadRequestError()
  }

  @Test
  fun `return an empty list, for unknown prisoner`() {
    assertGetOpenApplicationsIsOk("prisonNumber=X9876YZ", expectedResponseListOf())
  }

  @Nested
  @DisplayName("Given no application has been made, for the given prisoner")
  inner class GivenNoApplication {
    @Test
    fun `return a default paginated list of open applications`() {
      assertGetOpenApplicationsIsOk("prisonNumber=${knownApplicant.prisonNumber}", expectedResponseListOf())
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
    fun `return a paginated list of open applications`() {
      assertGetOpenApplicationsIsOk(
        parameters = "prisonNumber=${knownApplicant.prisonNumber}",
        expectedResponse = expectedResponseListOf(
          size = 10,
          page = 0,
          totalElements = 2,
          applicationToAmazonForkliftOperator.responseBody,
          applicationToTescoWarehouseHandler.responseBody,
        ),
      )
    }

    @Nested
    @DisplayName("And a custom pagination has been set")
    inner class CustomPagination {
      @Test
      fun `return a custom paginated list of open applications`() {
        assertGetOpenApplicationsIsOk(
          parameters = "prisonNumber=${knownApplicant.prisonNumber}&page=1&size=1",
          expectedResponse = expectedResponseListOf(
            size = 1,
            page = 1,
            totalElements = 2,
            applicationToTescoWarehouseHandler.responseBody,
          ),
        )
      }
    }
  }
}
