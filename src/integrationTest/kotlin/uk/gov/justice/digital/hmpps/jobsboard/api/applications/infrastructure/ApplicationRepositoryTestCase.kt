package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonABC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.applicationsFromPrisonXYZ
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.JobRepositoryTestCase

abstract class ApplicationRepositoryTestCase : JobRepositoryTestCase() {
  @Autowired
  protected lateinit var applicationRepository: ApplicationRepository

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
      employerRepository.save(it.job.employer)
      val job = jobRepository.saveAndFlush(it.job)
      val savedApplication = ApplicationMother.builder().from(it).apply { this.job = job }.build().run {
        applicationRepository.saveAndFlush(this)
      }
      savedApplications.add(savedApplication)
    }
    return savedApplications
  }

  private fun applicationBuilder(job: Job? = null) = ApplicationMother.builder().apply {
    ApplicationMother.knownApplicant.let {
      prisonNumber = it.prisonNumber
      firstName = it.firstName
      lastName = it.lastName
      prisonId = it.prisonId
      job?.let { this.job = job }
    }
  }
}
