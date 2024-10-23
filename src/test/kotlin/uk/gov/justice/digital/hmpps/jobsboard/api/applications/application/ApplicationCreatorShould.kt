package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationMother
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationMother.createApplicationRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ApplicationCreatorShould {
  @Mock
  protected lateinit var applicationRepository: ApplicationRepository

  @Mock
  protected lateinit var jobRepository: JobRepository

  @Mock
  protected lateinit var matchingCandidateJobsRepository: MatchingCandidateJobRepository

  @InjectMocks
  private lateinit var applicationCreator: ApplicationCreator

  @Nested
  @DisplayName("Given a valid job can be applied")
  inner class GivenAJobCanBeApplied {
    private val application = ApplicationMother.applicationToTescoWarehouseHandler

    @BeforeEach
    fun setUp() {
      val job = application.job
      whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
      whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumber(job.id.id, application.prisonNumber))
        .thenReturn(listOf(GetMatchingCandidateJobResponse.from(job)))
    }

    @Test
    fun `save the application with valid job`() {
      applicationCreator.createOrUpdate(application.createApplicationRequest)

      val actual = argumentCaptor<Application>().also { captor ->
        verify(applicationRepository).save(captor.capture())
      }.firstValue
      assertThat(actual).usingRecursiveComparison().isEqualTo(application)
    }

    @Test
    fun `throw exception, when the job has been archived for the prisoner`() {
      val job = application.job
      whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumber(job.id.id, application.prisonNumber))
        .thenReturn(listOf(GetMatchingCandidateJobResponse.from(job, archived = true)))

      val exception = assertFailsWith<IllegalArgumentException> {
        applicationCreator.createOrUpdate(application.createApplicationRequest)
      }
      assertEquals(
        "Job has been archived for the prisoner: jobId=${job.id}, prisonNumber=${application.prisonNumber}",
        exception.message,
      )
    }
  }

  @Nested
  @DisplayName("Given no job available")
  inner class GivenNoJobCanBeApplied {
    private val application = ApplicationMother.applicationToAmazonForkliftOperator

    @BeforeEach
    fun setUp() {
      application.job.let {
        whenever(jobRepository.findById(it.id)).thenReturn(Optional.empty())
      }
    }

    @Test
    fun `throw exception, when job is not found`() {
      val job = application.job
      val exception = assertFailsWith<IllegalArgumentException> {
        applicationCreator.createOrUpdate(application.createApplicationRequest)
      }
      assertEquals("Job not found: jobId=${job.id}", exception.message)
    }
  }
}
