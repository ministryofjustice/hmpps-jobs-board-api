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

  @Test
  fun `retrieve by prison number`() {
    val expectedJob = this.obtainTheJobJustCreated()

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual).isNotNull()
    assertThat(actual!!.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.hasExpressionOfInterest()).isFalse()
    assertThat(actual.isArchived()).isFalse()
  }

  @Test
  fun `retrieve with relevant ExpressionOfInterest and Archived`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      val makeMoreRecords: (String) -> Unit = { prisonNumber ->
        this.registerExpressionOfInterest(prisonNumber).also { expressionOfInterestRepository.save(it) }
        this.archivedBy(prisonNumber).also { archivedRepository.save(it) }
      }

      makeMoreRecords(expectedPrisonNumber)
      makeMoreRecords(unexpectedPrisonNumber)
      makeMoreRecords("X7777YZ")
      makeMoreRecords("U5555ST")
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual!!.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.expressionOfInterest!!.id.prisonNumber).isEqualTo(expectedPrisonNumber)
    assertThat(actual.archived!!.id.prisonNumber).isEqualTo(expectedPrisonNumber)
    assertThat(actual.hasExpressionOfInterest()).isTrue()
    assertThat(actual.isArchived()).isTrue()
  }

  @Test
  fun `retrieve without relevant ExpressionOfInterest nor Archived`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      registerExpressionOfInterest(unexpectedPrisonNumber).also { expressionOfInterestRepository.save(it) }
      archivedBy(unexpectedPrisonNumber).also { archivedRepository.save(it) }
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual!!.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.expressionOfInterest).isNull()
    assertThat(actual.archived).isNull()
    assertThat(actual.hasExpressionOfInterest()).isFalse()
    assertThat(actual.isArchived()).isFalse()
  }

  @Test
  fun `retrieve with relevant ExpressionOfInterest only`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      registerExpressionOfInterest(expectedPrisonNumber).also { expressionOfInterestRepository.save(it) }
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual!!.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.hasExpressionOfInterest()).isTrue()
    assertThat(actual.isArchived()).isFalse()
  }

  @Test
  fun `retrieve with relevant Archived only`() {
    val expectedJob = obtainTheJobJustCreated().apply {
      archivedBy(expectedPrisonNumber).also { archivedRepository.save(it) }
    }.also { entityManager.flush() }

    val actual = findJobDetailsByPrisonNumber(expectedJob.id.id, expectedPrisonNumber)

    assertThat(actual!!.job).usingRecursiveComparison().ignoringFields("expressionsOfInterest", "archived")
      .isEqualTo(expectedJob)
    assertThat(actual.hasExpressionOfInterest()).isFalse()
    assertThat(actual.isArchived()).isTrue()
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
  ): MatchingCandidateJobDetails? =
    matchingCandidateJobRepository.findJobDetailsByPrisonNumber(jobId, prisonNumber).also { searchResults ->
      assertThat(searchResults.size).isEqualTo(
        if (isExistingJob) 1 else 0,
      )
    }.firstOrNull()
}
