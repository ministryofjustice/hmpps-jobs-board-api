package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.deepCopy
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ExpressionOfInterestCreatorShould : TestBase() {

  @Mock
  protected lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @InjectMocks
  private lateinit var expressionOfInterestCreator: ExpressionOfInterestCreator

  private val expectedPrisonNumber = "A1234BC"

  private val expressionsOfInterestRequest = CreateExpressionOfInterestRequest.from(
    jobId = amazonForkliftOperator.id.id,
    prisonNumber = expectedPrisonNumber,
  )

  @Test
  fun `save with valid Job ID and prisonNumber`() {
    val expectedExpressionOfInterest = obtainTheJobJustCreated().let { job ->
      makeExpressionOfInterest(job, expectedPrisonNumber)
    }

    expressionOfInterestCreator.createOrUpdate(expressionsOfInterestRequest)

    val actualExpressionOfInterest = argumentCaptor<ExpressionOfInterest>().also { captor ->
      verify(expressionOfInterestRepository).save(captor.capture())
    }.firstValue
    assertThat(actualExpressionOfInterest).usingRecursiveComparison().isEqualTo(expectedExpressionOfInterest)
  }

  @Test
  fun `throw exception, when Job ID is empty at creation`() {
    val badRequest = CreateExpressionOfInterestRequest.from(
      jobId = "",
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestCreator.createOrUpdate(badRequest)
    }
    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception, when Job ID is invalid at creation`() {
    val invalidUUID = "00000000-0000-0000-0000-00000"
    val badRequest = CreateExpressionOfInterestRequest.from(
      jobId = invalidUUID,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestCreator.createOrUpdate(badRequest)
    }
    assertEquals("EntityId cannot be null: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception, when Job does NOT exist at creation`() {
    val nonExistentJobId = UUID.randomUUID().toString()
    val badRequest = CreateExpressionOfInterestRequest.from(
      jobId = nonExistentJobId,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestCreator.createOrUpdate(badRequest)
    }
    assertEquals("Job not found: jobId = $nonExistentJobId", exception.message)
  }

  @Test
  fun `throw exception, when prisoner's prisonNumber is invalid at creation`() {
    givenAJobIsCreated()

    val badRequest = CreateExpressionOfInterestRequest.from(
      jobId = amazonForkliftOperator.id.id,
      prisonNumber = "A1234BCZ",
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestCreator.createOrUpdate(badRequest)
    }
    assertEquals("prisonNumber is too long", exception.message)
  }

  @Test
  fun `return true when ExpressionOfInterest exists`() {
    givenAJobIsCreated()
    whenever(expressionOfInterestRepository.existsById(makeExpressionOfInterestId(amazonForkliftOperator.id.id, expectedPrisonNumber)))
      .thenReturn(true)

    val isExisting = expressionOfInterestCreator.existsById(amazonForkliftOperator.id.id, expectedPrisonNumber)
    assertThat(isExisting).isTrue()
  }

  @Test
  fun `return false when ExpressionOfInterest not exist`() {
    givenAJobIsCreated()
    whenever(expressionOfInterestRepository.existsById(makeExpressionOfInterestId(amazonForkliftOperator.id.id, expectedPrisonNumber)))
      .thenReturn(false)

    val isExisting = expressionOfInterestCreator.existsById(amazonForkliftOperator.id.id, expectedPrisonNumber)
    assertThat(isExisting).isFalse()
  }

  @Test
  fun `throw exception, when Job does NOT exist at ExpressionOfInterest's existence check`() {
    val nonExistentJobId = UUID.randomUUID().toString()

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestCreator.existsById(nonExistentJobId, expectedPrisonNumber)
    }
    assertEquals("Job not found: jobId=$nonExistentJobId", exception.message)
  }

  private fun givenAJobIsCreated() {
    obtainTheJobJustCreated()
  }

  private fun obtainTheJobJustCreated(): Job {
    return deepCopy(amazonForkliftOperator).also { job ->
      whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
    }
  }

  private fun makeExpressionOfInterestId(jobId: String, prisonNumber: String) =
    JobPrisonerId(EntityId(jobId), prisonNumber)

  private fun makeExpressionOfInterest(job: Job, prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = JobPrisonerId(job.id, prisonNumber), job = job)
}
