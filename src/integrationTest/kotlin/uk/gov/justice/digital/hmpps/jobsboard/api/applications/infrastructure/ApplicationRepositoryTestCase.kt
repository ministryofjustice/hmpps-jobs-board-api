package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.JobRepositoryTestCase

@Transactional(propagation = Propagation.NOT_SUPPORTED)
abstract class ApplicationRepositoryTestCase : JobRepositoryTestCase() {
  @Autowired
  protected lateinit var applicationRepository: ApplicationRepository

  @AfterEach
  fun tearDown() {
    applicationRepository.deleteAll()
  }
}
