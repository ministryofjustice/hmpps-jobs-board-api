package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.anotherUserTestName
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.longUsername
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.userTestName
import java.time.Duration
import java.util.*

class ArchivedRepositoryShould : JobRepositoryTestCase() {

  @Autowired
  private lateinit var archivedRepository: ArchivedRepository

  private val jobArchivedTime = jobCreationTime.plus(Duration.ofDays(1))
  private val jobRedoArchivedTime = jobCreationTime.plus(Duration.ofDays(2))

  @Test
  fun `archive a job for given prisoner`() {
    val job = givenAJobHasBeenCreated()

    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { jobArchived ->
      archivedRepository.saveAndFlush(jobArchived)
    }

    val jobArchivedFreshCopy =
      archivedRepository.findById(savedJobArchived.id).orElseThrow()
    assertThat(jobArchivedFreshCopy).usingRecursiveComparison().isEqualTo(savedJobArchived)
  }

  @Test
  fun `set audit attributes (createdBy, createdAt), when archiving job`() {
    val job = givenAJobHasBeenCreated()

    val jobArchived = makeJobArchived(job, expectedPrisonNumber)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobArchivedTime))
    val savedJobArchived = archivedRepository.saveAndFlush(jobArchived)

    with(savedJobArchived) {
      assertThat(createdAt).isEqualTo(jobArchivedTime)
      assertThat(createdBy).isEqualTo(userTestName)
    }
  }

  @Test
  fun `do NOT update job's attribute, when archiving job`() {
    val job = givenAJobHasBeenCreated()

    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { jobArchived ->
      archivedRepository.saveAndFlush(jobArchived)
    }

    val jobFreshCopy = jobRepository.findById(savedJobArchived.job.id).orElseThrow()
    assertThat(jobFreshCopy).usingRecursiveComparison().ignoringFields("expressionsOfInterest").isEqualTo(job)
    assertThat(jobFreshCopy.lastModifiedAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `do NOT archive again, when it exists`() {
    val job = givenAJobHasBeenCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { jobArchived ->
      archivedRepository.saveAndFlush(jobArchived)
    }

    val duplicateArchived = makeJobArchived(
      job = savedJobArchived.job,
      prisonNumber = savedJobArchived.id.prisonNumber,
    )
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobRedoArchivedTime))
    setCurrentAuditor(anotherUserTestName)
    val updatedArchived = archivedRepository.saveAndFlush(duplicateArchived).also {
      entityManager.refresh(it)
    }

    with(updatedArchived) {
      assertThat(createdAt).isEqualTo(jobArchivedTime)
      assertThat(createdBy).isEqualTo(userTestName)
      assertThat(this).usingRecursiveComparison().isEqualTo(savedJobArchived)
    }
  }

  @Test
  fun `throw exception, when archiving a non-existent job`() {
    val jobId = nonExistentJob.id.toString()
    val jobArchived = makeJobArchived(nonExistentJob, expectedPrisonNumber)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobArchivedTime))

    val exception = assertThrows<Exception> {
      archivedRepository.save(jobArchived)
    }

    assertThat(exception).isInstanceOf(DataAccessException::class.java)
    assertThat(exception.message)
      .contains("Unable to find")
      .contains(jobId)
  }

  @Test
  fun `undo archiving a job for given prisoner`() {
    val job = givenAJobHasBeenCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { jobArchived ->
      archivedRepository.saveAndFlush(jobArchived)
    }

    archivedRepository.deleteById(savedJobArchived.id)

    val searchArchived = archivedRepository.findById(savedJobArchived.id)
    assertThat(searchArchived.isEmpty).isTrue()
  }

  @Test
  fun `do NOT update job's attribute, when undo archiving`() {
    val job = givenAJobHasBeenCreated()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobArchivedTime))
    val savedJobArchived = makeJobArchived(job, expectedPrisonNumber).let { jobArchived ->
      archivedRepository.saveAndFlush(jobArchived)
    }

    archivedRepository.deleteById(savedJobArchived.id)

    val searchJob = jobRepository.findById(job.id).orElseThrow()
    assertThat(searchJob).usingRecursiveComparison().isEqualTo(job)
  }

  @Nested
  @DisplayName("Given a long username")
  inner class GivenALongUsername {
    @BeforeEach
    fun setUp() {
      setCurrentAuditor(longUsername)
    }

    @Test
    fun `archive a job with a long username`() {
      val job = givenAJobHasBeenCreated()

      val jobArchived = makeJobArchived(job, expectedPrisonNumber)
      val savedJobArchived = archivedRepository.saveAndFlush(jobArchived)

      assertThat(savedJobArchived.createdBy).hasSize(240)
    }
  }

  private fun makeJobArchived(job: Job, prisonNumber: String): Archived = Archived(id = JobPrisonerId(job.id, prisonNumber), job = job)
}
