package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.deepCopy
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ArchivedDeleterShould : TestBase() {

  @Mock
  protected lateinit var archivedRepository: ArchivedRepository

  @InjectMocks
  private lateinit var archivedDeleter: ArchivedDeleter

  private val expectedPrisonNumber = "A1234BC"

  private val archivedRequest = DeleteArchivedRequest.from(
    jobId = amazonForkliftOperator.id.id,
    prisonNumber = expectedPrisonNumber,
  )

  @Test
  fun `delete, when it exists`() {
    val archived = obtainTheArchivedJustCreated(stubJob = false)
    archivedDeleter.delete(archivedRequest)

    verify(archivedRepository).deleteById(archived.id)
  }

  @Test
  fun `throw exception, when Job ID is empty at deletion`() {
    val badRequest = DeleteArchivedRequest.from(
      jobId = "",
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedDeleter.delete(badRequest)
    }
    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception, when Job ID is invalid at deletion`() {
    val invalidUUID = "00000000-0000-0000-0000-00000"
    val badRequest = DeleteArchivedRequest.from(
      jobId = invalidUUID,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedDeleter.delete(badRequest)
    }
    assertEquals("EntityId cannot be null: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception, when prisoner's prisonNumber is invalid at deletion`() {
    val badRequest = DeleteArchivedRequest.from(
      jobId = amazonForkliftOperator.id.id,
      prisonNumber = "A1234BCZ",
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedDeleter.delete(badRequest)
    }
    assertEquals("prisonNumber is too long", exception.message)
  }

  @Test
  fun `nothing deleted, when Archived does NOT exist at deletion`() {
    archivedDeleter.delete(archivedRequest)
  }

  @Test
  fun `return true when Archived exists`() {
    obtainTheArchivedJustCreated()
    whenever(archivedRepository.existsById(makeArchivedId(amazonForkliftOperator.id.id, expectedPrisonNumber)))
      .thenReturn(true)

    val isExisting = archivedDeleter.existsById(amazonForkliftOperator.id.id, expectedPrisonNumber)
    assertThat(isExisting).isTrue()
  }

  @Test
  fun `return false when Archived not exist`() {
    obtainTheJobJustCreated()
    whenever(archivedRepository.existsById(makeArchivedId(amazonForkliftOperator.id.id, expectedPrisonNumber)))
      .thenReturn(false)

    val isExisting = archivedDeleter.existsById(amazonForkliftOperator.id.id, expectedPrisonNumber)
    assertThat(isExisting).isFalse()
  }

  @Test
  fun `throw exception, when Job does NOT exist at Archived's existence check`() {
    val nonExistentJobId = UUID.randomUUID().toString()

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedDeleter.existsById(nonExistentJobId, expectedPrisonNumber)
    }
    assertEquals("Job not found: jobId=$nonExistentJobId", exception.message)
  }

  private fun obtainTheJobJustCreated(stubJob: Boolean = true): Job = deepCopy(amazonForkliftOperator).also { job ->
    if (stubJob) {
      whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
    }
  }

  private fun obtainTheArchivedJustCreated(stubJob: Boolean = true): Archived {
    val job = obtainTheJobJustCreated(stubJob)
    return makeArchived(job, expectedPrisonNumber).also {
      job.archived[expectedPrisonNumber] = it
    }
  }

  private fun makeArchivedId(jobId: String, prisonNumber: String) = JobPrisonerId(EntityId(jobId), prisonNumber)

  private fun makeArchived(job: Job, prisonNumber: String): Archived = Archived(id = JobPrisonerId(job.id, prisonNumber), job = job)
}
