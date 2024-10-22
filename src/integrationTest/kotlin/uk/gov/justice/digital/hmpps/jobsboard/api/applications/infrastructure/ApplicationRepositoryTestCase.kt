package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAbcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToAmazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationToTescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.JobRepositoryTestCase

@Transactional(propagation = Propagation.NOT_SUPPORTED)
abstract class ApplicationRepositoryTestCase : JobRepositoryTestCase() {
  @Autowired
  protected lateinit var applicationRepository: ApplicationRepository

  @AfterEach
  fun tearDown() {
    applicationRepository.deleteAll()
  }

  protected fun givenAnApplicationMade(): Application {
    val application = applicationBuilder(job = givenAJobHasBeenCreated()).build()
    return applicationRepository.saveAndFlush(application)
  }

  protected fun givenThreeApplicationsMade(): List<Application> {
    val applications = mutableListOf<Application>()
    listOf(
      applicationToTescoWarehouseHandler,
      applicationToAmazonForkliftOperator,
      applicationToAbcConstructionApprentice,
    ).forEach {
      employerRepository.save(it.job.employer)
      val job = jobRepository.saveAndFlush(it.job)
      val savedApplication = ApplicationMother.builder().from(it).apply { this.job = job }.build().run {
        applicationRepository.saveAndFlush(this)
      }
      applications.add(savedApplication)
    }
    return applications
  }

  private fun applicationBuilder(job: Job? = null) = ApplicationMother.builder().apply {
    ApplicationMother.KnownApplicant.let {
      prisonNumber = it.prisonNumber
      firstName = it.firstName
      lastName = it.lastName
      prisonId = it.prisonId
      job?.let { this.job = job }
    }
  }
}
