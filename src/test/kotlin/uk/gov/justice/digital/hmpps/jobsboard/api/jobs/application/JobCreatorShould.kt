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
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.createJobRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
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

  @Nested
  @DisplayName("Given a postcode does not exist")
  inner class GivenPostcodeNotExisting {
    @Test
    fun `save a postcode with coordinates`() {
      val expectedPostcode = Postcode(
        id = EntityId(UUID.randomUUID().toString()),
        code = amazonForkliftOperator.postcode,
        xCoordinate = 1.23f,
        yCoordinate = 4.56f,
      )
      whenever(employerRepository.findById(amazon.id))
        .thenReturn(Optional.of(amazon))
      whenever(postcodeLocationService.getPostcodeFrom(amazonForkliftOperator.postcode))
        .thenReturn(expectedPostcode)

      jobCreator.createOrUpdate(amazonForkliftOperator.createJobRequest)

      val postcodeCaptor = argumentCaptor<Postcode>()
      verify(postcodesRepository).save(postcodeCaptor.capture())
      val actualPostcode = postcodeCaptor.firstValue

      assertThat(actualPostcode).isEqualTo(expectedPostcode)
    }
  }

  @Test
  fun `save a Job with valid details`() {
    whenever(employerRepository.findById(amazon.id))
      .thenReturn(Optional.of(amazon))

    jobCreator.createOrUpdate(amazonForkliftOperator.createJobRequest)

    val jobCaptor = argumentCaptor<Job>()
    verify(jobRepository).save(jobCaptor.capture())
    val actualJob = jobCaptor.firstValue

    assertThat(actualJob).usingRecursiveComparison().isEqualTo(amazonForkliftOperator)
  }

  @Test
  fun `throw an exception when saving a Job with an invalid UUID`() {
    whenever(employerRepository.findById(amazon.id))
      .thenReturn(Optional.of(amazon))

    val createJobRequest = builder().from(amazonForkliftOperator)
      .withId("invalid-uuid")
      .buildCreateJobRequest()

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.createOrUpdate(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("Invalid UUID format: {${createJobRequest.id}}")
  }

  @Test
  fun `throw exception when saving a Job with an empty UUID`() {
    whenever(employerRepository.findById(amazon.id))
      .thenReturn(Optional.of(amazon))

    val createJobRequest = builder().from(amazonForkliftOperator)
      .withId("")
      .buildCreateJobRequest()

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.createOrUpdate(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be empty")
  }

  @Test
  fun `throw an exception when saving a Job with a null UUID`() {
    whenever(employerRepository.findById(amazon.id))
      .thenReturn(Optional.of(amazon))

    val createJobRequest = builder().from(amazonForkliftOperator)
      .withId("00000000-0000-0000-0000-00000")
      .buildCreateJobRequest()

    val exception = assertThrows<IllegalArgumentException> {
      jobCreator.createOrUpdate(createJobRequest)
    }

    verify(jobRepository, never()).save(any(Job::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be null: {${createJobRequest.id}}")
  }
}
