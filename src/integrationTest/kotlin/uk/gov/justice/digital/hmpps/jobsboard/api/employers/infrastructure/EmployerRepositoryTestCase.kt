package uk.gov.justice.digital.hmpps.jobsboard.api.employers.infrastructure

import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.infrastructure.RepositoryTestCase
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.employerCreationTime
import java.util.*

abstract class EmployerRepositoryTestCase : RepositoryTestCase() {
  @Autowired
  protected lateinit var employerRepository: EmployerRepository

  @BeforeEach
  override fun setUp() {
    super.setUp()
    employerRepository.deleteAll()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerCreationTime))
  }
}
