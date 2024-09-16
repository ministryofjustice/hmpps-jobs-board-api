package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MatchingCandidateJobRepositoryJobDetailsShould : JobRepositoryTestCase() {

  @Autowired
  private lateinit var matchingCandidateJobRepository: MatchingCandidateJobRepository

  @Autowired
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @Autowired
  private lateinit var archivedRepository: ArchivedRepository

  private val unexpectedPrisonNumber = "Z4321YX"

  private data class JobDetailsQueryResult(
    val job: Job,
    val expressionOfInterest: ExpressionOfInterest?,
    val archived: Archived?,
  )

  @Test
  fun `retrieve by prison number`() {
    val expectedJob = this.obtainTheJobJustCreated()

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.expressionOfInterest).isNull()
    assertThat(actual.archived).isNull()
  }

  @Test
  fun `retrieve with proper ExpressionOfInterest and Archived`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      val makeMoreRecords: (String) -> Unit = { prisonNumber ->
        registerExpressionOfInterest(prisonNumber).also { expressionOfInterestRepository.save(it) }
        archivedBy(prisonNumber).also { archivedRepository.save(it) }
      }

      makeMoreRecords(expectedPrisonNumber)
      makeMoreRecords(unexpectedPrisonNumber)
      makeMoreRecords("X7777YZ")
      makeMoreRecords("U5555ST")
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.expressionOfInterest!!.id.prisonNumber).isEqualTo(expectedPrisonNumber)
    assertThat(actual.archived!!.id.prisonNumber).isEqualTo(expectedPrisonNumber)
  }

  @Test
  fun `retrieve without irrelevant ExpressionOfInterest nor Archived`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      registerExpressionOfInterest(unexpectedPrisonNumber).also { expressionOfInterestRepository.save(it) }
      archivedBy(unexpectedPrisonNumber).also { archivedRepository.save(it) }
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.expressionOfInterest).isNull()
    assertThat(actual.archived).isNull()
  }

  @Test
  fun `retrieve with ExpressionOfInterest only`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      registerExpressionOfInterest(expectedPrisonNumber).also { expressionOfInterestRepository.save(it) }
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.expressionOfInterest!!.id.prisonNumber).isEqualTo(expectedPrisonNumber)
    assertThat(actual.archived).isNull()
  }

  @Test
  fun `retrieve with Archived only`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      archivedBy(expectedPrisonNumber).also { archivedRepository.save(it) }
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.expressionOfInterest).isNull()
    assertThat(actual.archived!!.id.prisonNumber).isEqualTo(expectedPrisonNumber)
  }

  @Test
  fun `retrieve nothing when job does not exist`() {
    val searchResults = matchingCandidateJobRepository.findJobDetailsByPrisonNumber(
      jobId = nonExistentJob.id.id,
      prisonNumber = expectedPrisonNumber,
    )
    assertThat(searchResults).isEmpty()
  }

  private fun findJobDetailsByPrisonNumber(jobId: String, prisonNumber: String): JobDetailsQueryResult =
    matchingCandidateJobRepository.findJobDetailsByPrisonNumber(jobId, prisonNumber).also { searchResults ->
      assertThat(searchResults.size).isEqualTo(1)
    }.first().let { jobDetails ->
      JobDetailsQueryResult(
        job = jobDetails[0] as Job,
        expressionOfInterest = jobDetails[1] as ExpressionOfInterest?,
        archived = jobDetails[2] as Archived?,
      )
    }

  private fun Job.archivedBy(prisonNumber: String): Archived =
    Archived(id = JobPrisonerId(this.id, prisonNumber), job = this)

  private fun Job.registerExpressionOfInterest(prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = JobPrisonerId(this.id, prisonNumber), job = this)
}
