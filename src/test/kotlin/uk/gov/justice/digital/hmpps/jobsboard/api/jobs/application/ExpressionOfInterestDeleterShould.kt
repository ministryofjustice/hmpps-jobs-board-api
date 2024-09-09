package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ExpressionOfInterestDeleterShould : TestBase() {

  @Mock
  protected lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @InjectMocks
  private lateinit var expressionOfInterestDeleter: ExpressionOfInterestDeleter

  private val expectedJobId = "fe5d5175-5a21-4cec-a30b-fd87a5f76ce7"
  private val expectedPrisonNumber = "A1234BC"

  private val expressionsOfInterestRequest = DeleteExpressionOfInterestRequest.from(
    jobId = expectedJobId,
    prisonNumber = expectedPrisonNumber,
  )

  @Test
  fun `delete, when it exists`() {
    val expressionOfInterest = obtainTheExpressionOfInterestJustCreated(stubJob = false)
    expressionOfInterestDeleter.delete(expressionsOfInterestRequest)

    verify(expressionOfInterestRepository).deleteById(expressionOfInterest.id)
  }

  @Test
  fun `throw exception, when Job ID is empty at deletion`() {
    val badRequest = DeleteExpressionOfInterestRequest.from(
      jobId = "",
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestDeleter.delete(badRequest)
    }
    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception, when Job ID is invalid at deletion`() {
    val invalidUUID = "00000000-0000-0000-0000-00000"
    val badRequest = DeleteExpressionOfInterestRequest.from(
      jobId = invalidUUID,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestDeleter.delete(badRequest)
    }
    assertEquals("EntityId cannot be null: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception, when prisoner's prisonNumber is invalid at deletion`() {
    val badRequest = DeleteExpressionOfInterestRequest.from(
      jobId = expectedJobId,
      prisonNumber = "A1234BCZ",
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestDeleter.delete(badRequest)
    }
    assertEquals("prisonNumber is too long", exception.message)
  }

  @Test
  fun `nothing deleted, when ExpressionOfInterest does NOT exist at deletion`() {
    expressionOfInterestDeleter.delete(expressionsOfInterestRequest)
  }

  @Test
  fun `return true when ExpressionOfInterest exists`() {
    obtainTheExpressionOfInterestJustCreated()
    whenever(expressionOfInterestRepository.existsById(makeExpressionOfInterestId(expectedJobId, expectedPrisonNumber)))
      .thenReturn(true)

    val isExisting = expressionOfInterestDeleter.existsById(expectedJobId, expectedPrisonNumber)
    assertThat(isExisting).isTrue()
  }

  @Test
  fun `return false when ExpressionOfInterest not exist`() {
    obtainTheJobJustCreated()
    whenever(expressionOfInterestRepository.existsById(makeExpressionOfInterestId(expectedJobId, expectedPrisonNumber)))
      .thenReturn(false)

    val isExisting = expressionOfInterestDeleter.existsById(expectedJobId, expectedPrisonNumber)
    assertThat(isExisting).isFalse()
  }

  @Test
  fun `throw exception, when Job does NOT exist at ExpressionOfInterest's existence check`() {
    val nonExistentJobId = UUID.randomUUID().toString()

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestDeleter.existsById(nonExistentJobId, expectedPrisonNumber)
    }
    assertEquals("Job not found: jobId=$nonExistentJobId", exception.message)
  }

  private fun obtainTheJobJustCreated(stubJob: Boolean = true): Job {
    return deepCopy(expectedJob).also { job ->
      if (stubJob) {
        whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
      }
    }
  }

  private fun obtainTheExpressionOfInterestJustCreated(stubJob: Boolean = true): ExpressionOfInterest {
    val job = obtainTheJobJustCreated(stubJob)
    return makeExpressionOfInterest(job, expectedPrisonNumber).also {
      job.expressionsOfInterest[expectedPrisonNumber] = it
    }
  }

  private fun makeExpressionOfInterestId(jobId: String, prisonNumber: String) =
    ExpressionOfInterestId(EntityId(jobId), prisonNumber)

  private fun makeExpressionOfInterest(job: Job, prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = ExpressionOfInterestId(job.id, prisonNumber), job = job)
}
