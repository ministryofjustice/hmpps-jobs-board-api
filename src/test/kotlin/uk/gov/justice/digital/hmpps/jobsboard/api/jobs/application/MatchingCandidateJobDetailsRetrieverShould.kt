package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobDetails
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class MatchingCandidateJobDetailsRetrieverShould : TestBase() {

  @Mock
  private lateinit var matchingCandidateJobsRepository: MatchingCandidateJobRepository

  @InjectMocks
  private lateinit var matchingCandidateJobDetailsRetriever: MatchingCandidateJobDetailsRetriever

  private val expectedJobId = expectedJob.id
  private val expectedPrisonNumber = "A1234BC"

  @Test
  fun `return JobDetails without prisonNumber, when found`() {
    whenever(jobRepository.findById(expectedJobId)).thenReturn(Optional.of(expectedJob))

    val actualMatchingCandidateJobDetails: MatchingCandidateJobDetails? =
      matchingCandidateJobDetailsRetriever.retrieve(expectedJobId.id)

    verify(jobRepository, times(1)).findById(expectedJobId)
    assertThat(actualMatchingCandidateJobDetails!!.job).isEqualTo(expectedJob)
    assertThat(actualMatchingCandidateJobDetails.expressionOfInterest).isNull()
    assertThat(actualMatchingCandidateJobDetails.hasExpressionOfInterest()).isFalse()
    assertThat(actualMatchingCandidateJobDetails.archived).isNull()
    assertThat(actualMatchingCandidateJobDetails.isArchived()).isFalse()
  }

  @Test
  fun `return JobDetails with prisonNumber, when found`() {
    val job = expectedJob.deepCopyMe()
    val expectedMatchingCandidateJobDetails = MatchingCandidateJobDetails(
      job = job,
      expressionOfInterest = job.makeExpressionOfInterest(expectedPrisonNumber),
      archived = job.makeArchived(expectedPrisonNumber),
    )
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber))
      .thenReturn(listOf(expectedMatchingCandidateJobDetails))

    val actualMatchingCandidateJobDetails: MatchingCandidateJobDetails? =
      matchingCandidateJobDetailsRetriever.retrieve(expectedJobId.id, expectedPrisonNumber)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber)
    assertThat(actualMatchingCandidateJobDetails!!).usingRecursiveComparison()
      .isEqualTo(expectedMatchingCandidateJobDetails)
    assertThat(actualMatchingCandidateJobDetails.expressionOfInterest).usingRecursiveComparison()
      .isEqualTo(expectedMatchingCandidateJobDetails.expressionOfInterest)
    assertThat(actualMatchingCandidateJobDetails.hasExpressionOfInterest()).isTrue()
    assertThat(actualMatchingCandidateJobDetails.archived).usingRecursiveComparison()
      .isEqualTo(expectedMatchingCandidateJobDetails.archived)
    assertThat(actualMatchingCandidateJobDetails.isArchived()).isTrue()
  }

  @Test
  fun `return JobDetails with prisonNumber and ExpressionOfInterest only, when found`() {
    val job = expectedJob.deepCopyMe()
    val expectedMatchingCandidateJobDetails = MatchingCandidateJobDetails(
      job = job,
      expressionOfInterest = job.makeExpressionOfInterest(expectedPrisonNumber),
    )
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber))
      .thenReturn(listOf(expectedMatchingCandidateJobDetails))

    val actualMatchingCandidateJobDetails: MatchingCandidateJobDetails? =
      matchingCandidateJobDetailsRetriever.retrieve(expectedJobId.id, expectedPrisonNumber)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber)
    assertThat(actualMatchingCandidateJobDetails!!.hasExpressionOfInterest()).isTrue()
    assertThat(actualMatchingCandidateJobDetails.isArchived()).isFalse()
  }

  @Test
  fun `return JobDetails with prisonNumber and Archived only, when found`() {
    val job = expectedJob.deepCopyMe()
    val expectedMatchingCandidateJobDetails = MatchingCandidateJobDetails(
      job = job,
      archived = job.makeArchived(expectedPrisonNumber),
    )
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber))
      .thenReturn(listOf(expectedMatchingCandidateJobDetails))

    val actualMatchingCandidateJobDetails: MatchingCandidateJobDetails? =
      matchingCandidateJobDetailsRetriever.retrieve(expectedJobId.id, expectedPrisonNumber)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber)
    assertThat(actualMatchingCandidateJobDetails!!.hasExpressionOfInterest()).isFalse()
    assertThat(actualMatchingCandidateJobDetails.isArchived()).isTrue()
  }

  @Test
  fun `return nothing without prisonNumber, when not found`() {
    whenever(jobRepository.findById(expectedJobId)).thenReturn(Optional.empty())

    val actualMatchingCandidateJobDetails: MatchingCandidateJobDetails? =
      matchingCandidateJobDetailsRetriever.retrieve(expectedJobId.id)

    verify(jobRepository, times(1)).findById(expectedJobId)
    assertThat(actualMatchingCandidateJobDetails).isNull()
  }

  @Test
  fun `return nothing with prisonNumber, when not found`() {
    val job = expectedJob.deepCopyMe()
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber))
      .thenReturn(listOf())

    val actualMatchingCandidateJobDetails: MatchingCandidateJobDetails? =
      matchingCandidateJobDetailsRetriever.retrieve(expectedJobId.id, expectedPrisonNumber)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumber(expectedJobId.id, expectedPrisonNumber)
    assertThat(actualMatchingCandidateJobDetails).isNull()
  }

  private fun Job.makeExpressionOfInterest(prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = JobPrisonerId(this.id, prisonNumber), job = this).also {
      this.expressionsOfInterest.put(prisonNumber, it)
    }

  private fun Job.makeArchived(prisonNumber: String): Archived =
    Archived(id = JobPrisonerId(this.id, prisonNumber), job = this).also {
      this.archived.put(prisonNumber, it)
    }
}
