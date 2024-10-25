package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationMother
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationMother.applicationToAbcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationMother.applicationToAmazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationMother.applicationToTescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus.APPLICATION_MADE
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus.UNSUCCESSFUL_AT_INTERVIEW

@ExtendWith(MockitoExtension::class)
class ApplicationRetrieverShould {
  @Mock
  protected lateinit var applicationRepository: ApplicationRepository

  @Mock
  private lateinit var pageable: Pageable

  @InjectMocks
  private lateinit var applicationRetriever: ApplicationRetriever

  @Nested
  @DisplayName("Given open and closed applications have been made")
  inner class GivenOpenAndClosedApplicationsMade {
    private val prisonId = ApplicationMother.KnownApplicant.prisonId

    private val openApplications = listOf(applicationToTescoWarehouseHandler, applicationToAmazonForkliftOperator)
    private val closedApplications = listOf(applicationToAbcConstructionApprentice)
    private val allApplications = openApplications + closedApplications

    private val openApplicationStatus = listOf(APPLICATION_MADE).map { it.toString() }
    private val closedApplicationStatus = listOf(UNSUCCESSFUL_AT_INTERVIEW).map { it.toString() }

    @Test
    fun `return all applications of the given prison`() {
      whenever(applicationRepository.findByPrisonId(prisonId, pageable)).thenReturn(PageImpl(allApplications))

      val applications = applicationRetriever.retrieveAllApplicationsByPrisonId(prisonId, pageable = pageable)
      assertThat(applications)
        .isNotEmpty.hasSize(allApplications.size)
        .isEqualTo(PageImpl(allApplications))
    }

    @Test
    fun `return open applications of the given prison, when application status has been specified`() {
      mockRepoCall(openApplicationStatus, openApplications)
      assertApplications(openApplicationStatus, openApplications)
    }

    @Test
    fun `return closed applications of the given prison, when application status has been specified`() {
      mockRepoCall(closedApplicationStatus, closedApplications)
      assertApplications(closedApplicationStatus, closedApplications)
    }

    private fun assertApplications(status: List<String>, expectedApplications: List<Application>): Page<Application> {
      val applications =
        applicationRetriever.retrieveAllApplicationsByPrisonId(prisonId, status = status, pageable = pageable)
      assertThat(applications)
        .isNotEmpty.hasSize(expectedApplications.size)
        .isEqualTo(PageImpl(expectedApplications))

      verify(applicationRepository, times(1))
        .findByPrisonIdAndPrisonerNameAndApplicationStatusAndJobTitleOrEmployerName(
          prisonId,
          null,
          status,
          null,
          pageable,
        )
      return applications
    }

    private fun mockRepoCall(status: List<String>, expectedApplications: List<Application>) {
      whenever(
        applicationRepository.findByPrisonIdAndPrisonerNameAndApplicationStatusAndJobTitleOrEmployerName(
          prisonId = prisonId,
          status = status,
          prisonerName = null,
          jobTitleOrEmployerName = null,
          pageable = pageable,
        ),
      ).thenReturn(PageImpl(expectedApplications))
    }
  }
}
