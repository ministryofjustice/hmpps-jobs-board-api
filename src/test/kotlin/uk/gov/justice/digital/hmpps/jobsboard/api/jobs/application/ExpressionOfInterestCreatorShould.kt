package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
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

  private val expectedJobId = "fe5d5175-5a21-4cec-a30b-fd87a5f76ce7"
  private val expectedPrisonNumber = "A1234BC"

  private val expressionsOfInterestRequest = CreateExpressionOfInterestRequest.from(
    jobId = expectedJobId,
    prisonNumber = expectedPrisonNumber,
  )

  @Test
  fun `save with valid Job ID and prisonNumber, when it does NOT exist`() {
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
  fun `save and return true, when it does NOT exist`() {
    obtainTheJobJustCreated().also { job -> makeExpressionOfInterest(job, expectedPrisonNumber) }

    val created = expressionOfInterestCreator.createOrUpdate(expressionsOfInterestRequest)
    assertThat(created).isTrue()
  }

  @Test
  fun `do NOT save ExpressionOfInterest again, and return false, when it exists`() {
    givenAJobIsCreatedWithExpressionOfInterest()

    val created = expressionOfInterestCreator.createOrUpdate(expressionsOfInterestRequest)

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(created).isFalse()
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
    assertEquals("Job not found: jobId=$nonExistentJobId", exception.message)
  }

  @Test
  fun `throw exception, when prisoner's prisonNumber is invalid at creation`() {
    givenAJobIsCreated()

    val badRequest = CreateExpressionOfInterestRequest.from(
      jobId = expectedJobId,
      prisonNumber = "A1234BCZ",
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestCreator.createOrUpdate(badRequest)
    }
    assertEquals("prisonNumber is too long", exception.message)
  }

  private fun givenAJobIsCreated() {
    obtainTheJobJustCreated()
  }

  private fun givenAJobIsCreatedWithExpressionOfInterest() {
    obtainTheJobJustCreated().let { job ->
      makeExpressionOfInterest(job, expectedPrisonNumber).also {
        job.expressionsOfInterest[expectedPrisonNumber] = it
      }
    }
  }

  private fun obtainTheJobJustCreated(): Job {
    return deepCopy(expectedJob).also { job ->
      whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
    }
  }

  private fun makeExpressionOfInterest(job: Job, prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = ExpressionOfInterestId(job.id, prisonNumber), job = job)
}
