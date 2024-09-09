package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
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
    val expressionOfInterest = obtainTheExpressionOfInterestJustCreated()
    val id = expressionsOfInterestRequest.let { request ->
      ExpressionOfInterestId(
        EntityId(request.jobId),
        request.prisonNumber,
      )
    }
    whenever(expressionOfInterestRepository.findById(id)).thenReturn(Optional.of(expressionOfInterest))

    expressionOfInterestDeleter.delete(expressionsOfInterestRequest)

    verify(expressionOfInterestRepository).deleteById(id)
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
    givenAJobIsCreatedWithExpressionOfInterest()
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
  fun `return false, when ExpressionOfInterest does NOT exist at deletion`() {
    givenAJobIsCreatedWithExpressionOfInterest()
    val deleted = expressionOfInterestDeleter.delete(expressionsOfInterestRequest)

    verify(expressionOfInterestRepository, never()).deleteById(any(ExpressionOfInterestId::class.java))
    assertThat(deleted).isFalse()
  }

  private fun givenAJobIsCreatedWithExpressionOfInterest() {
    obtainTheExpressionOfInterestJustCreated()
  }

  private fun obtainTheJobJustCreated(): Job {
    return deepCopy(expectedJob).also { job ->
      whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
    }
  }

  private fun obtainTheExpressionOfInterestJustCreated(): ExpressionOfInterest =
    obtainTheJobJustCreated().let { job ->
      makeExpressionOfInterest(job, expectedPrisonNumber).also {
        job.expressionsOfInterest[expectedPrisonNumber] = it
      }
    }

  private fun makeExpressionOfInterest(job: Job, prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = ExpressionOfInterestId(job.id, prisonNumber), job = job)
}
