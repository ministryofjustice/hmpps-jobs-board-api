package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider

@ExtendWith(MockitoExtension::class)
abstract class TestBase {

  @Mock
  protected lateinit var jobRepository: JobRepository

  @Mock
  protected lateinit var employerRepository: EmployerRepository

  @Mock
  protected lateinit var postcodeLocationService: PostcodeLocationService

  @Mock
  protected lateinit var outboundEventsService: OutboundEventsService

  @Mock
  protected lateinit var uuidGenerator: UUIDGenerator

  @Mock
  protected lateinit var timeProvider: TimeProvider

  protected val objectMapper: ObjectMapper = jacksonObjectMapper()
}
