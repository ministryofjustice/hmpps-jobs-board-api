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
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ExpressionOfInterestEditorShould : TestBase() {

  @Mock
  protected lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @InjectMocks
  private lateinit var expressionOfInterestEditor: ExpressionOfInterestEditor

  private val expectedJobId = "fe5d5175-5a21-4cec-a30b-fd87a5f76ce7"
  private val expectedPrisonNumber = "A1234BC"

  private val expressionsOfInterestRequest = CreateOrDeleteExpressionOfInterestRequest.from(
    jobId = expectedJobId,
    prisonNumber = expectedPrisonNumber,
  )

  @Test
  fun `save with valid Job-ID and Prison-Number, when it does NOT exist`() {
    val expectedExpressionOfInterest = obtainTheJobJustCreated().let { job ->
      makeExpressionOfInterest(job, expectedPrisonNumber)
    }

    expressionOfInterestEditor.createWhenNotExist(expressionsOfInterestRequest)

    val actualExpressionOfInterest = argumentCaptor<ExpressionOfInterest>().also { captor ->
      verify(expressionOfInterestRepository).save(captor.capture())
    }.firstValue
    assertThat(actualExpressionOfInterest).usingRecursiveComparison().isEqualTo(expectedExpressionOfInterest)
  }

  @Test
  fun `save and return true, when it does NOT exist`() {
    obtainTheJobJustCreated().also { job -> makeExpressionOfInterest(job, expectedPrisonNumber) }

    val created = expressionOfInterestEditor.createWhenNotExist(expressionsOfInterestRequest)
    assertThat(created).isTrue()
  }

  @Test
  fun `do NOT save expression-of-interest again, and return false, when it exists`() {
    givenAJobIsCreatedWithExpressionOfInterest()

    val created = expressionOfInterestEditor.createWhenNotExist(expressionsOfInterestRequest)

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(created).isFalse()
  }

  @Test
  fun `throw exception, when Job-ID is empty at creation`() {
    val badRequest = CreateOrDeleteExpressionOfInterestRequest.from(
      jobId = "",
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestEditor.createWhenNotExist(badRequest)
    }
    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception, when Job-ID is invalid at creation`() {
    val invalidUUID = "00000000-0000-0000-0000-00000"
    val badRequest = CreateOrDeleteExpressionOfInterestRequest.from(
      jobId = invalidUUID,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestEditor.createWhenNotExist(badRequest)
    }
    assertEquals("EntityId cannot be null: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception, when job does NOT exist at creation`() {
    val nonExistentJobId = UUID.randomUUID().toString()
    val badRequest = CreateOrDeleteExpressionOfInterestRequest.from(
      jobId = nonExistentJobId,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestEditor.createWhenNotExist(badRequest)
    }
    assertEquals("Job not found: jobId=$nonExistentJobId", exception.message)
  }

  @Test
  fun `throw exception, when prisoner's Prison-Number is invalid at creation`() {
    givenAJobIsCreated()

    val badRequest = CreateOrDeleteExpressionOfInterestRequest.from(
      jobId = expectedJobId,
      prisonNumber = "A1234BCZ",
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestEditor.createWhenNotExist(badRequest)
    }
    assertEquals("prisonNumber is too long", exception.message)
  }

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

    expressionOfInterestEditor.delete(expressionsOfInterestRequest)

    verify(expressionOfInterestRepository).deleteById(id)
  }

  @Test
  fun `throw exception, when Job-ID is empty at deletion`() {
    val badRequest = CreateOrDeleteExpressionOfInterestRequest.from(
      jobId = "",
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestEditor.delete(badRequest)
    }
    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception, when Job-ID is invalid at deletion`() {
    val invalidUUID = "00000000-0000-0000-0000-00000"
    val badRequest = CreateOrDeleteExpressionOfInterestRequest.from(
      jobId = invalidUUID,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestEditor.delete(badRequest)
    }
    assertEquals("EntityId cannot be null: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception, when prisoner's Prison-Number is invalid at deletion`() {
    givenAJobIsCreatedWithExpressionOfInterest()
    val badRequest = CreateOrDeleteExpressionOfInterestRequest.from(
      jobId = expectedJobId,
      prisonNumber = "A1234BCZ",
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      expressionOfInterestEditor.delete(badRequest)
    }
    assertEquals("prisonNumber is too long", exception.message)
  }

  @Test
  fun `return false, when expression-of-interest does NOT exist at deletion`() {
    givenAJobIsCreatedWithExpressionOfInterest()
    val deleted = expressionOfInterestEditor.delete(expressionsOfInterestRequest)

    verify(expressionOfInterestRepository, never()).deleteById(any(ExpressionOfInterestId::class.java))
    assertThat(deleted).isFalse()
  }

  private fun givenAJobIsCreated() {
    obtainTheJobJustCreated()
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
