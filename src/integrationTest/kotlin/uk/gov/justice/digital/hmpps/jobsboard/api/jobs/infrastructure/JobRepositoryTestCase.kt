package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.infrastructure.RepositoryTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobPrisonerId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes
import java.time.Instant
import java.util.*

abstract class JobRepositoryTestCase : RepositoryTestCase() {

  @Autowired
  protected lateinit var employerRepository: EmployerRepository

  @Autowired
  protected lateinit var jobRepository: JobRepository

  protected final val jobCreationTime: Instant = TestPrototypes.jobCreationTime
  protected final val expectedPrisonNumber = TestPrototypes.VALID_PRISON_NUMBER
  protected final val nonExistentJob = TestPrototypes.nonExistentJob

  @BeforeEach
  override fun setUp() {
    jobRepository.deleteAll()
    employerRepository.deleteAll()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobCreationTime))
  }

  protected fun givenAJobHasBeenCreated(): Job {
    employerRepository.save(amazon)
    return jobRepository.save(amazonForkliftOperator).also {
      entityManager.flush()
    }
  }

  protected fun Job.archivedBy(prisonNumber: String): Archived =
    Archived(id = JobPrisonerId(this.id, prisonNumber), job = this)

  protected fun Job.registerExpressionOfInterest(prisonNumber: String): ExpressionOfInterest =
    ExpressionOfInterest(id = JobPrisonerId(this.id, prisonNumber), job = this)
}
