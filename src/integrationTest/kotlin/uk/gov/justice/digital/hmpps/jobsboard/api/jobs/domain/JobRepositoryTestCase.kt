package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.RepositoryTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import java.time.Instant
import java.util.*

abstract class JobRepositoryTestCase : RepositoryTestCase() {

  @Autowired
  protected lateinit var employerRepository: EmployerRepository

  @Autowired
  protected lateinit var jobRepository: JobRepository

  protected final val jobCreationTime: Instant = TestPrototypes.jobCreationTime
  protected final val jobModificationTime: Instant = TestPrototypes.jobModificationTime
  protected final val expectedPrisonNumber = TestPrototypes.expectedPrisonNumber

  protected final val amazonEmployer = TestPrototypes.amazonEmployer
  protected final val amazonForkliftOperatorJob = TestPrototypes.amazonForkliftOperatorJob

  protected final val nonExistentJob = TestPrototypes.nonExistentJob

  @BeforeEach
  override fun setUp() {
    jobRepository.deleteAll()
    employerRepository.deleteAll()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobCreationTime))
  }

  protected fun obtainTheJobJustCreated(): Job {
    employerRepository.save(amazonEmployer)
    return jobRepository.save(amazonForkliftOperatorJob).also {
      entityManager.flush()
    }
  }

  protected fun Job.archivedBy(prisonNumber: String): Archived =
    Archived(id = JobPrisonerId(this.id, prisonNumber), job = this)

  protected fun Job.registerExpressionOfInterest(prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = JobPrisonerId(this.id, prisonNumber), job = this)
}
