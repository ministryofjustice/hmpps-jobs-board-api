package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository

class MatchingCandidateJobRepositoryJobDetailsShould : JobRepositoryTestCase() {

  @Autowired
  private lateinit var matchingCandidateJobRepository: MatchingCandidateJobRepository

  @Autowired
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @Autowired
  private lateinit var archivedRepository: ArchivedRepository

  private val unexpectedPrisonNumber = "Z4321YX"

  @Test
  fun `retrieve by prison number`() {
    val expectedJob = this.givenAJobHasBeenCreated()

    assertJobDetailsByPrisonNumber(expectedJob, expectedPrisonNumber, false, false)
  }

  @Test
  fun `retrieve with relevant ExpressionOfInterest and Archived`() {
    val expectedJob = givenAJobHasBeenCreated().apply {
      val makeMoreRecords: (String) -> Unit = { prisonNumber ->
        this.registerExpressionOfInterest(prisonNumber).also { expressionOfInterestRepository.save(it) }
        this.archivedBy(prisonNumber).also { archivedRepository.save(it) }
      }

      makeMoreRecords(expectedPrisonNumber)
      makeMoreRecords(unexpectedPrisonNumber)
      makeMoreRecords("X7777YZ")
      makeMoreRecords("U5555ST")
    }.also { entityManager.flush() }

    assertJobDetailsByPrisonNumber(expectedJob, expectedPrisonNumber, true, true)
  }

  @Test
  fun `retrieve without relevant ExpressionOfInterest nor Archived`() {
    val expectedJob = givenAJobHasBeenCreated().apply {
      registerExpressionOfInterest(unexpectedPrisonNumber).also { expressionOfInterestRepository.save(it) }
      archivedBy(unexpectedPrisonNumber).also { archivedRepository.save(it) }
    }.also { entityManager.flush() }

    assertJobDetailsByPrisonNumber(expectedJob, expectedPrisonNumber, false, false)
  }

  @Test
  fun `retrieve with relevant ExpressionOfInterest only`() {
    val expectedJob = givenAJobHasBeenCreated().apply {
      registerExpressionOfInterest(expectedPrisonNumber).also { expressionOfInterestRepository.save(it) }
    }.also { entityManager.flush() }

    assertJobDetailsByPrisonNumber(expectedJob, expectedPrisonNumber, true, false)
  }

  @Test
  fun `retrieve with relevant Archived only`() {
    val expectedJob = givenAJobHasBeenCreated().apply {
      archivedBy(expectedPrisonNumber).also { archivedRepository.save(it) }
    }.also { entityManager.flush() }

    assertJobDetailsByPrisonNumber(expectedJob, expectedPrisonNumber, false, true)
  }

  @Test
  fun `retrieve nothing when job does not exist`() {
    val jobDetails = findJobDetailsByPrisonNumber(
      jobId = nonExistentJob.id.id,
      prisonNumber = expectedPrisonNumber,
      isExistingJob = false,
    )
    assertThat(jobDetails).isNull()
  }

  private fun findJobDetailsByPrisonNumber(
    jobId: String,
    prisonNumber: String,
    isExistingJob: Boolean = true,
  ): GetMatchingCandidateJobResponse? = matchingCandidateJobRepository.findJobDetailsByPrisonNumber(jobId, prisonNumber).also { searchResults ->
    assertThat(searchResults.size).isEqualTo(
      if (isExistingJob) 1 else 0,
    )
  }.firstOrNull()

  private fun assertJobDetailsByPrisonNumber(
    expectedJob: Job,
    expectedPrisonNumber: String,
    expectedExpessionOfInterest: Boolean? = null,
    expectedArchived: Boolean? = null,
    expectedDistance: Float? = null,
  ): GetMatchingCandidateJobResponse? {
    val actualJobDetails = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actualJobDetails).isNotNull.usingRecursiveComparison()
      .ignoringFields(
        "id",
        "jobTitle",
        "employerName",
        "closingDate",
        "startDate",
        "offenceExclusions",
        "createdAt",
        "distance",
        "expressionOfInterest",
        "archived",
      )
      .isEqualTo(expectedJob)
    assertThat(actualJobDetails!!.id).isEqualTo(expectedJob.id.id)
    assertThat(actualJobDetails.jobTitle).isEqualTo(expectedJob.title)
    assertThat(actualJobDetails.closingDate).isEqualTo(expectedJob.closingDate?.toString())
    assertThat(actualJobDetails.startDate).isEqualTo(expectedJob.startDate?.toString())
    assertThat(actualJobDetails.offenceExclusions?.joinToString(separator = ",")).isEqualTo(expectedJob.offenceExclusions)
    assertThat(actualJobDetails.createdAt).isEqualTo(expectedJob.createdAt.toString())
    expectedDistance?.let { assertThat(actualJobDetails.distance).isEqualTo(it) }
    expectedExpessionOfInterest?.let { assertThat(actualJobDetails.expressionOfInterest).isEqualTo(it) }
    expectedArchived?.let { assertThat(actualJobDetails.archived).isEqualTo(it) }

    return actualJobDetails
  }
}
