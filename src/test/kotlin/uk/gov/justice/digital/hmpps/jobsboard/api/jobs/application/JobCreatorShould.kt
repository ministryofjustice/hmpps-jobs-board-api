package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobEventType
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.createJobRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEvent
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class JobCreatorShould : TestBase() {

  @InjectMocks
  private lateinit var jobCreator: JobCreator

  @Test
  fun `return true when Job exists`() {
    val jobId = EntityId(UUID.randomUUID().toString())
    whenever(jobRepository.existsById(jobId)).thenReturn(true)

    assertThat(jobCreator.existsById(jobId.id)).isTrue()
  }

  @Test
  fun `return false when Job does not exist`() {
    val jobId = EntityId(UUID.randomUUID().toString())
    whenever(jobRepository.existsById(jobId)).thenReturn(false)

    assertThat(jobCreator.existsById(jobId.id)).isFalse()
  }

  @Test
  fun `save the job's postcode via postCodeLocationService`() {
    givenEmployerCreated(amazon)
    wheneverCreateOutboundEvent()

    jobCreator.create(amazonForkliftOperator.createJobRequest)

    val postcodeCaptor = argumentCaptor<String>()
    verify(postcodeLocationService).save(postcodeCaptor.capture())
    val actualPostcode = postcodeCaptor.firstValue

    assertThat(actualPostcode).isEqualTo(amazonForkliftOperator.postcode)
  }

  @Test
  fun `save a Job with valid details`() {
    givenEmployerCreated(amazon)
    wheneverCreateOutboundEvent()

    jobCreator.create(amazonForkliftOperator.createJobRequest)

    val jobCaptor = argumentCaptor<Job>()
    verify(jobRepository).save(jobCaptor.capture())
    val actualJob = jobCaptor.firstValue

    assertThat(actualJob).usingRecursiveComparison().isEqualTo(amazonForkliftOperator)
  }

  @Test
  fun `throw an exception when saving a Job with an invalid UUID`() {
    givenEmployerCreated(amazon)

    val createJobRequest = builder().from(amazonForkliftOperator)
      .withId("invalid-uuid")
      .buildCreateJobRequest()

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.create(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("Invalid UUID format: {${createJobRequest.id}}")
  }

  @Test
  fun `throw exception when saving a Job with an empty UUID`() {
    givenEmployerCreated(amazon)

    val createJobRequest = builder().from(amazonForkliftOperator)
      .withId("")
      .buildCreateJobRequest()

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.create(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be empty")
  }

  @Test
  fun `throw an exception when saving a Job with a null UUID`() {
    givenEmployerCreated(amazon)

    val createJobRequest = builder().from(amazonForkliftOperator)
      .withId("00000000-0000-0000-0000-00000")
      .buildCreateJobRequest()

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.create(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be null: {${createJobRequest.id}}")
  }

  @Test
  fun `throw an exception when creating a CreateJobRequest with a null postcode and isNational=false`() {
    val exception = assertThrows<IllegalArgumentException> {
      builder().from(amazonForkliftOperator)
        .withPostcode(null)
        .buildCreateJobRequest()
    }

    assertThat(exception.message).isEqualTo("Postcode must be provided if job is not national")
  }

  @Test
  fun `throw an exception when creating a Job with a null postcode and isNational=false`() {
    val exception = assertThrows<IllegalArgumentException> {
      builder().from(amazonForkliftOperator)
        .withPostcode(null)
        .build()
    }

    assertThat(exception.message).isEqualTo("Postcode must be provided if job is not national")
  }

  @Nested
  @DisplayName("Given Integration has been enabled")
  inner class GivenIntegrationEnabled {
    private val job = amazonForkliftOperator
    private val createJobRequest = amazonForkliftOperator.createJobRequest

    @Test
    fun `send event after saving a new Job`() {
      givenEmployerCreated()
      wheneverCreateOutboundEvent()

      jobCreator.create(createJobRequest)

      assertEvent(JobEventType.JOB_CREATED)
    }

    @Test
    fun `send event after saving an existing Job`() {
      givenEmployerCreated()
      givenJobCreated()

      jobCreator.update(createJobRequest)

      assertEvent(JobEventType.JOB_UPDATED)
    }

    private fun assertEvent(eventType: JobEventType) {
      val eventCaptor = argumentCaptor<OutboundEvent>()
      verify(outboundEventsService).handleMessage(eventCaptor.capture())
      val actualEvent = eventCaptor.firstValue

      assertThat(actualEvent.eventType).isEqualTo(eventType.type)
      assertThat(actualEvent.content).isNotBlank()
      val payloadJson = objectMapper.readTree(actualEvent.content)
      assertThat(payloadJson.get("jobId").textValue()).isEqualTo(createJobRequest.id)
      assertThat(payloadJson.get("eventType").textValue()).isEqualTo(eventType.eventTypeCode)
    }

    private fun givenJobCreated() {
      wheneverCreateOutboundEvent()
      whenever(jobRepository.findById(job.id)).thenReturn(Optional.of(job))
    }

    private fun givenEmployerCreated() {
      givenEmployerCreated(job.employer)
    }
  }

  private fun givenEmployerCreated(employer: Employer) {
    whenever(employerRepository.findById(employer.id)).thenReturn(Optional.of(employer))
  }

  private fun wheneverCreateOutboundEvent() {
    whenever(uuidGenerator.generate()).thenReturn(UUID.randomUUID().toString())
    whenever(timeProvider.nowAsInstant()).thenReturn(Instant.now())
  }
}
