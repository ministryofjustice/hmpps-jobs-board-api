package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonABC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonXYZ
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.JobRepositoryTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure.TestClock
import java.time.Instant
import java.util.*

abstract class ApplicationRepositoryTestCase : JobRepositoryTestCase() {
  @Autowired
  protected lateinit var applicationRepository: ApplicationRepository

  @Autowired
  protected lateinit var applicationAuditCleaner: ApplicationAuditCleaner

  protected val testClock: TestClock = TestClock.defaultClock()
  protected val currentTime: Instant get() = testClock.instant

  override fun setUp() {
    super.setUp()
    applicationAuditCleaner.deleteAllRevisions()
    whenever(dateTimeProvider.now).thenAnswer { Optional.of(currentTime) }
  }

  protected fun givenAnApplicationMade(): Application {
    val application = applicationBuilder(job = givenAJobHasBeenCreated()).build()
    return applicationRepository.saveAndFlush(application)
  }

  protected fun givenThreeApplicationsMade() = givenApplicationsMade(applicationsFromPrisonMDI)

  protected fun givenMoreApplicationsFromMultiplePrisons() {
    (applicationsFromPrisonMDI + applicationsFromPrisonABC + applicationsFromPrisonXYZ).let { applications ->
      givenApplicationsMade(applications)
    }
  }

  protected fun givenApplicationsMade(applications: List<Application>): List<Application> {
    val savedApplications = mutableListOf<Application>()
    applications.forEach {
      givenApplicationMade(it).let {
        savedApplications.add(it)
      }
    }
    return savedApplications
  }

  protected fun givenApplicationMade(application: Application): Application {
    employerRepository.save(application.job.employer)
    val job = jobRepository.saveAndFlush(application.job)

    val builder = ApplicationMother.builder().from(application).apply { this.job = job }

    // insert APPLICATION_MADE status prior to expected status, for any other expected status, for auditing tests
    when (application.status) {
      ApplicationStatus.APPLICATION_MADE.name -> {}
      else -> {
        builder.status = ApplicationStatus.APPLICATION_MADE.name
        builder.build().run { applicationRepository.saveAndFlush(this) }
        builder.status = application.status
      }
    }

    return builder.build().run { applicationRepository.saveAndFlush(this) }
  }

  protected fun applicationBuilder(job: Job? = null) = ApplicationMother.builder().apply {
    ApplicationMother.knownApplicant.let {
      prisonNumber = it.prisonNumber
      firstName = it.firstName
      lastName = it.lastName
      prisonId = it.prisonId
      job?.let { this.job = job }
    }
  }
}
