package uk.gov.justice.digital.hmpps.jobsboard.api.employers.infrastructure

import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.employerCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure.RepositoryTestCase
import java.util.*

abstract class EmployerRepositoryTestCase : RepositoryTestCase() {

  @BeforeEach
  override fun setUp() {
    super.setUp()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerCreationTime))
  }
}
