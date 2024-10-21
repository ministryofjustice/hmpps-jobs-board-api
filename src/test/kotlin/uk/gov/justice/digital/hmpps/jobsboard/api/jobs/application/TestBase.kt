package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository

@ExtendWith(MockitoExtension::class)
abstract class TestBase {

  @Mock
  protected lateinit var jobRepository: JobRepository

  @Mock
  protected lateinit var employerRepository: EmployerRepository

  @Mock
  protected lateinit var postcodesRepository: PostcodesRepository

  @Mock
  protected lateinit var postcodeLocationService: PostcodeLocationService
}
