package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.VALID_PRISON_NUMBER
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobRegisterExpressionOfInterestTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.nonExistentJob
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

class ExpressionOfInterestRepositoryShould : JobRepositoryTestCase() {
  @Autowired
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @Test
  fun `save prisoner's ExpressionOfInterest to an existing job`() {
    val job = obtainTheJobJustCreated()

    val savedExpressionOfInterest = makeExpressionOfInterest(job, VALID_PRISON_NUMBER).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    val expressionOfInterestFreshCopy =
      expressionOfInterestRepository.findById(savedExpressionOfInterest.id).orElseThrow()
    assertThat(expressionOfInterestFreshCopy).usingRecursiveComparison().isEqualTo(savedExpressionOfInterest)
  }

  @Test
  fun `set createdAt attribute, when saving a new ExpressionOfInterest`() {
    val job = obtainTheJobJustCreated()

    val expressionOfInterest = makeExpressionOfInterest(job, VALID_PRISON_NUMBER)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = expressionOfInterestRepository.saveAndFlush(expressionOfInterest)

    assertThat(savedExpressionOfInterest.createdAt).isEqualTo(jobRegisterExpressionOfInterestTime)
  }

  @Test
  fun `do NOT update job's attribute, when saving a new ExpressionOfInterest`() {
    val job = obtainTheJobJustCreated()

    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, VALID_PRISON_NUMBER).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    val jobFreshCopy = jobRepository.findById(savedExpressionOfInterest.job.id).orElseThrow()
    assertThat(jobFreshCopy).usingRecursiveComparison().ignoringFields("expressionsOfInterest").isEqualTo(job)
    assertThat(jobFreshCopy.modifiedAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `do NOT update ExpressionOfInterest, when it exists`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, VALID_PRISON_NUMBER).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    val duplicateExpressionOfInterest = makeExpressionOfInterest(
      job = savedExpressionOfInterest.job,
      prisonNumber = savedExpressionOfInterest.id.prisonNumber,
    )
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime.plus(1, DAYS)))
    val updatedExpressionOfInterest = expressionOfInterestRepository.saveAndFlush(duplicateExpressionOfInterest).also {
      entityManager.refresh(it)
    }

    assertThat(updatedExpressionOfInterest).usingRecursiveComparison().isEqualTo(savedExpressionOfInterest)
    assertThat(updatedExpressionOfInterest.createdAt).isEqualTo(jobRegisterExpressionOfInterestTime)
  }

  @Test
  fun `throw exception, when saving ExpressionOfInterest with non-existent job`() {
    val jobId = nonExistentJob.id.toString()
    val expressionOfInterest = makeExpressionOfInterest(nonExistentJob, VALID_PRISON_NUMBER)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))

    val exception = assertThrows<Exception> {
      expressionOfInterestRepository.save(expressionOfInterest)
    }

    assertThat(exception).isInstanceOf(DataAccessException::class.java)
    assertThat(exception.message)
      .contains("Unable to find")
      .contains(jobId)
  }

  @Test
  fun `delete prisoner's ExpressionOfInterest from an existing job`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, VALID_PRISON_NUMBER).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    expressionOfInterestRepository.deleteById(savedExpressionOfInterest.id)

    val searchExpressionOfInterest = expressionOfInterestRepository.findById(savedExpressionOfInterest.id)
    assertThat(searchExpressionOfInterest.isEmpty).isTrue()
  }

  @Test
  fun `do NOT update job's attribute, when deleting existing ExpressionOfInterest`() {
    val job = obtainTheJobJustCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRegisterExpressionOfInterestTime))
    val savedExpressionOfInterest = makeExpressionOfInterest(job, VALID_PRISON_NUMBER).let { expressionOfInterest ->
      expressionOfInterestRepository.saveAndFlush(expressionOfInterest)
    }

    expressionOfInterestRepository.deleteById(savedExpressionOfInterest.id)

    val searchJob = jobRepository.findById(job.id).orElseThrow()
    assertThat(searchJob).usingRecursiveComparison().isEqualTo(job)
  }

  private fun obtainTheJobJustCreated(): Job {
    employerRepository.save(amazon)
    return jobRepository.save(amazonForkliftOperator).also {
      entityManager.flush()
    }
  }

  private fun makeExpressionOfInterest(job: Job, prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = JobPrisonerId(job.id, prisonNumber), job = job)
}
