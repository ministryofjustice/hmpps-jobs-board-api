package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicantA
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicantB
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicantC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicantD
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicantE
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAbcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAmazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToTescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsMap
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.knownApplicant
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonABC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.requestBody

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
  open inner class GivenSomeApplications {
    @BeforeEach
    fun setUp() = givenMoreApplicationsFromMultiplePrisons()

    @Test
    fun `return a default paginated applications list, for given prison`() {
      assertGetApplicationsByPrisonIdIsOk(prisonMDI, applicationsFromPrisonMDI)
    }

    private fun assertGetApplicationsByPrisonIdIsOk(
      prisonId: String,
      expectedApplications: List<Application> = listOf(),
      expectedPageSize: Int = defaultPageSize,
      expectedPage: Int = 0,
    ) {
      assertGetApplicationsIsOk(
        parameters = "prisonId=$prisonId",
        expectedResponse = expectedResponseListOf(
          size = expectedPageSize,
          page = expectedPage,
          elements = expectedApplications.map { it.searchResponseBody }.toTypedArray(),
        ),
      )
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    open fun `return applications list, that can be retained after the job updated, for given prison`() {
      val job = JobMother.builder().from(amazonForkliftOperator).apply {
        additionalSalaryInformation = "updated info about salary: ... "
      }.build()
      assertUpdateJobIsOk(job.id.id, job.requestBody)

      assertGetApplicationsByPrisonIdIsOk(prisonMDI, applicationsFromPrisonMDI)
    }

    @Nested
    @DisplayName("And more filters have been set.")
    inner class AndMoreFilters {
      @Test
      fun `return an applications list filtered by application status, for given prison and status`() {
        assertGetApplicationsFilterByApplicationStatusIsOk(
          prisonId = prisonMDI,
          applicationStatus = ApplicationStatus.APPLICATION_MADE.name,
          expectedApplications = listOf(
            applicationToAmazonForkliftOperator,
            applicationToTescoWarehouseHandler,
          ),
        )
      }

      @Test
      fun `return opened applications list, for given prison and status`() {
        assertGetApplicationsFilterByApplicationStatusIsOk(
          prisonId = prisonMDI,
          applicationStatus = ApplicationStatus.openStatus.map { it.name },
          expectedApplications = listOf(
            applicationToAmazonForkliftOperator,
            applicationToTescoWarehouseHandler,
          ),
        )
      }

      @Test
      fun `return closed applications list, for given prison and status`() {
        assertGetApplicationsFilterByApplicationStatusIsOk(
          prisonId = prisonMDI,
          applicationStatus = ApplicationStatus.closedStatus.map { it.name },
          expectedApplications = listOf(applicationToAbcConstructionApprentice),
        )
      }

      @Test
      fun `return an applications list filtered by prisoner name, for given prison and (upper case) search text matching first name`() {
        val searchText = "DOUBLE"
        val expectedApplications = applicantB.let {
          applicationsMap[it].orEmpty()
        }

        assertGetApplicationsFilterByPrisonerNameIsOk(prisonABC, searchText, expectedApplications)
      }

      @Test
      fun `return an applications list filtered by prisoner name, for given prison and (lower case) search text matching last name or full names`() {
        val searchText = "three half"
        val expectedApplications = listOf(applicantC, applicantD)
          .map { applicationsMap[it].orEmpty() }.flatten()

        assertGetApplicationsFilterByPrisonerNameIsOk(prisonABC, searchText, expectedApplications)
      }

      @Test
      fun `return an applications list filtered by prisoner name, for given prison and (mixed cases) search text matching either first or last names`() {
        val searchText = "thRee"
        val expectedApplications = listOf(applicantC, applicantD, applicantE)
          .map { applicationsMap[it].orEmpty() }.flatten()

        assertGetApplicationsFilterByPrisonerNameIsOk(prisonABC, searchText, expectedApplications)
      }

      @Test
      fun `return an applications list filtered by job title or employer name, for given prison and search text matching job title`() {
        assertGetApplicationsFilterByJobTitleOrEmployerNameIsOk(
          prisonId = prisonMDI,
          jobTitleOrEmployerName = "warehouse",
          expectedApplications = listOf(applicationToTescoWarehouseHandler),
        )
      }

      @Test
      fun `return an applications list filtered by job title or employer name, for given prison and search text matching employer name`() {
        assertGetApplicationsFilterByJobTitleOrEmployerNameIsOk(
          prisonId = prisonMDI,
          jobTitleOrEmployerName = "AMAzON",
          expectedApplications = listOf(applicationToAmazonForkliftOperator),
        )
      }

      private fun assertGetApplicationsFilterByApplicationStatusIsOk(
        prisonId: String,
        applicationStatus: String,
        expectedApplications: List<Application>,
      ) = assertGetApplicationsFilterByApplicationStatusIsOk(prisonId, expectedApplications, applicationStatus)

      private fun assertGetApplicationsFilterByApplicationStatusIsOk(
        prisonId: String,
        applicationStatus: List<String>,
        expectedApplications: List<Application>,
      ) = assertGetApplicationsFilterByApplicationStatusIsOk(
        prisonId,
        expectedApplications,
        *applicationStatus.toTypedArray(),
      )

      private fun assertGetApplicationsFilterByApplicationStatusIsOk(
        prisonId: String,
        expectedApplications: List<Application>,
        vararg applicationStatus: String,
      ) = assertGetApplicationsIsOk(
        parameters = "prisonId=$prisonId&applicationStatus=${applicationStatus.joinToString(",")}",
        expectedResponse = expectedResponseListOf(
          size = defaultPageSize,
          page = 0,
          elements = expectedApplications.map { it.searchResponseBody }.toTypedArray(),
        ),
      )

      private fun assertGetApplicationsFilterByJobTitleOrEmployerNameIsOk(
        prisonId: String,
        jobTitleOrEmployerName: String,
        expectedApplications: List<Application>,
      ) = assertGetApplicationsIsOk(
        parameters = "prisonId=$prisonId&jobTitleOrEmployerName=$jobTitleOrEmployerName",
        expectedResponse = expectedResponseListOf(
          size = defaultPageSize,
          page = 0,
          elements = expectedApplications.map { it.searchResponseBody }.toTypedArray(),
        ),
      )

      private fun assertGetApplicationsFilterByPrisonerNameIsOk(
        prisonId: String,
        prisonerName: String,
        expectedApplications: List<Application>,
      ) = assertGetApplicationsIsOk(
        parameters = "prisonId=$prisonId&prisonerName=$prisonerName",
        expectedResponse = expectedResponseListOf(
          size = defaultPageSize,
          page = 0,
          elements = expectedApplications.map { it.searchResponseBody }.toTypedArray(),
        ),
      )
    }

    @Nested
    @DisplayName("And custom pagination has been set.")
    inner class AndCustomPaginationSet {
      @Test
      fun `return a custom paginated applications list, for given prison`() {
        val prisonId = prisonMDI
        val expectedPageSize = 1
        val expectedPage = 1
        val expectedTotalElements = 3

        assertGetApplicationsIsOk(
          parameters = "prisonId=$prisonId&size=$expectedPageSize&page=$expectedPage",
          expectedResponse = expectedResponseListOf(
            size = expectedPageSize,
            page = expectedPage,
            totalElements = expectedTotalElements,
            applicationToTescoWarehouseHandler.searchResponseBody,
          ),
        )
      }
    }

    @Nested
    @DisplayName("And custom sorting has been set.")
    inner class AndCustomSortingSet {
      @Test
      fun `return a sorted application list, that is sorted by Job and Employer (desc), for given prison`() {
        val prisonId = prisonMDI
        val sortedApplications = listOf(
          applicationToTescoWarehouseHandler,
          applicationToAmazonForkliftOperator,
          applicationToAbcConstructionApprentice,
        )
        assertGetApplicationsIsSortedByJobAndEmployer(
          parameters = "prisonId=$prisonId&sortBy=jobAndEmployer&sortOrder=desc",
          expectedJobTitleSortedList = sortedApplications.map { it.job.title },
          expectedEmployerNameSortedList = sortedApplications.map { it.job.employer.name },
        )
      }

      @Test
      fun `return a sorted application list, that is sorted by prisoner's name (asc), for given prison`() {
        val prisonId = prisonABC
        val sortedApplicants = mapOf(
          applicantC to 1,
          applicantB to 3,
          applicantD to 1,
          applicantE to 1,
          applicantA to 3,
        ).map { entry -> List(entry.value) { entry.key } }.flatten()
        assertGetApplicationsIsSortedByPrisonerName(
          parameters = "prisonId=$prisonId&sortBy=prisonerName",
          expectedLastNameSortedList = sortedApplicants.map { it.lastName },
          expectedFirstNameSortedList = sortedApplicants.map { it.firstName },
        )
      }
    }
  }
}
