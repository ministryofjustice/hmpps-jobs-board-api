package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.deepCopyMe
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class MatchingCandidateJobDetailsRetrieverShould : TestBase() {

  @Mock
  private lateinit var matchingCandidateJobsRepository: MatchingCandidateJobRepository

  @InjectMocks
  private lateinit var matchingCandidateJobDetailsRetriever: MatchingCandidateJobDetailsRetriever

  private val expectedPrisonNumber = "A1234BC"
  private val releaseAreaPostcode = "S37BS"

  @Test
  fun `return JobDetails without prisonNumber, when found`() {
    whenever(jobRepository.findById(amazonForkliftOperator.id))
      .thenReturn(Optional.of(amazonForkliftOperator))
    val expectedMatchingCandidateJobDetails = GetMatchingCandidateJobResponse
      .from(amazonForkliftOperator, false, false)

    val actualMatchingCandidateJobDetails = matchingCandidateJobDetailsRetriever.retrieve(amazonForkliftOperator.id.id)
    verify(jobRepository, times(1)).findById(amazonForkliftOperator.id)
    assertThat(actualMatchingCandidateJobDetails!!).usingRecursiveComparison()
      .isEqualTo(expectedMatchingCandidateJobDetails)
    assertThat(actualMatchingCandidateJobDetails.expressionOfInterest).isFalse()
    assertThat(actualMatchingCandidateJobDetails.archived).isFalse()
  }

  @Test
  fun `return JobDetails with prisonNumber, when found`() {
    val job = amazonForkliftOperator.deepCopyMe()
    val expectedMatchingCandidateJobDetails = GetMatchingCandidateJobResponse.from(job, true, true)
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode))
      .thenReturn(listOf(expectedMatchingCandidateJobDetails))

    val actualMatchingCandidateJobDetails =
      matchingCandidateJobDetailsRetriever.retrieve(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)
    assertThat(actualMatchingCandidateJobDetails!!).usingRecursiveComparison()
      .isEqualTo(expectedMatchingCandidateJobDetails)
    assertThat(actualMatchingCandidateJobDetails.expressionOfInterest).isTrue()
    assertThat(actualMatchingCandidateJobDetails.archived).isTrue()
  }

  @Test
  fun `return JobDetails with prisonNumber and ExpressionOfInterest only, when found`() {
    val job = amazonForkliftOperator.deepCopyMe()
    val expectedMatchingCandidateJobDetails =
      GetMatchingCandidateJobResponse.from(job = job, expressionOfInterest = true)
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode))
      .thenReturn(listOf(expectedMatchingCandidateJobDetails))

    val actualMatchingCandidateJobDetails =
      matchingCandidateJobDetailsRetriever.retrieve(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)
    assertThat(actualMatchingCandidateJobDetails!!.expressionOfInterest).isTrue()
    assertThat(actualMatchingCandidateJobDetails.archived).isFalse()
  }

  @Test
  fun `return JobDetails with prisonNumber and Archived only, when found`() {
    val job = amazonForkliftOperator.deepCopyMe()
    val expectedMatchingCandidateJobDetails = GetMatchingCandidateJobResponse.from(job = job, archived = true)
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode))
      .thenReturn(listOf(expectedMatchingCandidateJobDetails))

    val actualMatchingCandidateJobDetails =
      matchingCandidateJobDetailsRetriever.retrieve(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)
    assertThat(actualMatchingCandidateJobDetails!!.expressionOfInterest).isFalse()
    assertThat(actualMatchingCandidateJobDetails.archived).isTrue()
  }

  @Test
  fun `return nothing without prisonNumber, when not found`() {
    whenever(jobRepository.findById(amazonForkliftOperator.id)).thenReturn(Optional.empty())

    val actualMatchingCandidateJobDetails =
      matchingCandidateJobDetailsRetriever.retrieve(amazonForkliftOperator.id.id)

    verify(jobRepository, times(1)).findById(amazonForkliftOperator.id)
    assertThat(actualMatchingCandidateJobDetails).isNull()
  }

  @Test
  fun `return nothing with prisonNumber, when not found`() {
    whenever(matchingCandidateJobsRepository.findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode))
      .thenReturn(listOf())

    val actualMatchingCandidateJobDetails =
      matchingCandidateJobDetailsRetriever.retrieve(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)

    verify(matchingCandidateJobsRepository, times(1))
      .findJobDetailsByPrisonNumberAndReleaseAreaPostcode(amazonForkliftOperator.id.id, expectedPrisonNumber, releaseAreaPostcode)
    assertThat(actualMatchingCandidateJobDetails).isNull()
  }
}
