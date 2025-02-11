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
class ArchivedCreatorShould : TestBase() {

  @Mock
  protected lateinit var archivedRepository: ArchivedRepository

  @InjectMocks
  private lateinit var archivedCreator: ArchivedCreator

  private val expectedPrisonNumber = "A1234BC"

  private val archivedRequest = CreateArchivedRequest.from(
    jobId = amazonForkliftOperator.id.id,
    prisonNumber = expectedPrisonNumber,
  )

  @Test
  fun `save with valid Job ID and prisonNumber`() {
    val expectedArchived = obtainTheJobJustCreated().let { job ->
      makeArchived(job, expectedPrisonNumber)
    }

    archivedCreator.createOrUpdate(archivedRequest)

    val actualArchived = argumentCaptor<Archived>().also { captor ->
      verify(archivedRepository).save(captor.capture())
    }.firstValue
    assertThat(actualArchived).usingRecursiveComparison().isEqualTo(expectedArchived)
  }

  @Test
  fun `throw exception, when Job ID is empty at creation`() {
    val badRequest = CreateArchivedRequest.from(
      jobId = "",
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedCreator.createOrUpdate(badRequest)
    }
    assertEquals("EntityId cannot be empty", exception.message)
  }

  @Test
  fun `throw exception, when Job ID is invalid at creation`() {
    val invalidUUID = "00000000-0000-0000-0000-00000"
    val badRequest = CreateArchivedRequest.from(
      jobId = invalidUUID,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedCreator.createOrUpdate(badRequest)
    }
    assertEquals("EntityId cannot be null: {$invalidUUID}", exception.message)
  }

  @Test
  fun `throw exception, when Job does NOT exist at creation`() {
    val nonExistentJobId = UUID.randomUUID().toString()
    val badRequest = CreateArchivedRequest.from(
      jobId = nonExistentJobId,
      prisonNumber = expectedPrisonNumber,
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedCreator.createOrUpdate(badRequest)
    }
    assertEquals("Job not found: jobId=$nonExistentJobId", exception.message)
  }

  @Test
  fun `throw exception, when prisoner's prisonNumber is invalid at creation`() {
    givenAJobIsCreated()

    val badRequest = CreateArchivedRequest.from(
      jobId = amazonForkliftOperator.id.id,
      prisonNumber = "A1234BCZ",
    )

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedCreator.createOrUpdate(badRequest)
    }
    assertEquals("prisonNumber is too long", exception.message)
  }

  @Test
  fun `return true when Archived exists`() {
    givenAJobIsCreated()
    whenever(archivedRepository.existsById(makeArchivedId(amazonForkliftOperator.id.id, expectedPrisonNumber)))
      .thenReturn(true)

    val isExisting = archivedCreator.existsById(amazonForkliftOperator.id.id, expectedPrisonNumber)
    assertThat(isExisting).isTrue()
  }

  @Test
  fun `return false when Archived not exist`() {
    givenAJobIsCreated()
    whenever(archivedRepository.existsById(makeArchivedId(amazonForkliftOperator.id.id, expectedPrisonNumber)))
      .thenReturn(false)

    val isExisting = archivedCreator.existsById(amazonForkliftOperator.id.id, expectedPrisonNumber)
    assertThat(isExisting).isFalse()
  }

  @Test
  fun `throw exception, when Job does NOT exist at Archived's existence check`() {
    val nonExistentJobId = UUID.randomUUID().toString()

    val exception = assertFailsWith<IllegalArgumentException> {
      archivedCreator.existsById(nonExistentJobId, expectedPrisonNumber)
    }
    assertEquals("Job not found: jobId=$nonExistentJobId", exception.message)
  }

  private fun givenAJobIsCreated() {
    obtainTheJobJustCreated()
  }

  private fun obtainTheJobJustCreated(): Job = deepCopy(amazonForkliftOperator).also { job ->
    whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
  }

  private fun makeArchivedId(jobId: String, prisonNumber: String) = JobPrisonerId(EntityId(jobId), prisonNumber)

  private fun makeArchived(job: Job, prisonNumber: String): Archived = Archived(id = JobPrisonerId(job.id, prisonNumber), job = job)
}
