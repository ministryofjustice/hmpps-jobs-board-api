package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
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
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerEventType
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.TestBase
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEvent
import java.time.Instant
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class EmployerCreatorShould : TestBase() {

  @InjectMocks
  private lateinit var employerCreator: EmployerCreator

  private val createEmployerRequest = CreateEmployerRequest.from(
    id = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
    sector = "RETAIL",
    status = "GOLD",
  )

  private val sainsburysEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
    sector = "RETAIL",
    status = "GOLD",
  )

  private val expectedEmployer = sainsburysEmployer

  @Test
  fun `return true when Employer exists`() {
    val employerId = EntityId(UUID.randomUUID().toString())
    whenever(employerRepository.existsById(employerId)).thenReturn(true)

    assertThat(employerCreator.existsById(employerId.id)).isTrue()
  }

  @Test
  fun `return false when Employer does not exist`() {
    val employerId = EntityId(UUID.randomUUID().toString())
    whenever(employerRepository.existsById(employerId)).thenReturn(false)

    assertThat(employerCreator.existsById(employerId.id)).isFalse()
  }

  @Test
  fun `save an Employer with valid details`() {
    wheneverCreateOutboundEvent()

    employerCreator.create(createEmployerRequest)

    val employerCaptor = argumentCaptor<Employer>()
    verify(employerRepository).save(employerCaptor.capture())
    val actualEmployer = employerCaptor.firstValue

    assertThat(actualEmployer.id).isEqualTo(expectedEmployer.id)
    assertThat(actualEmployer.name).isEqualTo(expectedEmployer.name)
    assertThat(actualEmployer.description).isEqualTo(expectedEmployer.description)
    assertThat(actualEmployer.sector).isEqualTo(expectedEmployer.sector)
    assertThat(actualEmployer.status).isEqualTo(expectedEmployer.status)
  }

  @Test
  fun `save an Employer with current time`() {
    wheneverCreateOutboundEvent()

    employerCreator.create(createEmployerRequest)

    val employerCaptor = argumentCaptor<Employer>()
    verify(employerRepository).save(employerCaptor.capture())
    val actualEmployer = employerCaptor.firstValue

    assertThat(actualEmployer.createdAt).isEqualTo(expectedEmployer.createdAt)
    assertThat(actualEmployer.lastModifiedAt).isEqualTo(expectedEmployer.lastModifiedAt)
  }

  @Test
  fun `throw an exception when saving an Employer with an invalid UUID`() {
    val createEmployerRequest = CreateEmployerRequest.from(
      id = "invalid-uuid",
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      sector = "sector",
      status = "status",
    )

    val exception = assertThrows<IllegalArgumentException> {
      employerCreator.create(createEmployerRequest)
    }

    verify(employerRepository, never()).save(any(Employer::class.java))
    assertThat(exception.message).isEqualTo("Invalid UUID format: {${createEmployerRequest.id}}")
  }

  @Test
  fun `throw an exception when saving an Employer with an empty UUID`() {
    val createEmployerRequest = CreateEmployerRequest.from(
      id = "",
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      sector = "sector",
      status = "status",
    )

    val exception = assertThrows<IllegalArgumentException> {
      employerCreator.create(createEmployerRequest)
    }

    verify(employerRepository, never()).save(any(Employer::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be empty")
  }

  @Test
  fun `throw an exception when saving an Employer with a null UUID`() {
    val createEmployerRequest = CreateEmployerRequest.from(
      id = "00000000-0000-0000-0000-00000",
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      sector = "sector",
      status = "status",
    )

    val exception = assertThrows<IllegalArgumentException> {
      employerCreator.create(createEmployerRequest)
    }

    verify(employerRepository, never()).save(any(Employer::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be null: {${createEmployerRequest.id}}")
  }

  @Nested
  @DisplayName("Given Integration has been enabled")
  inner class GivenIntegrationEnabled {
    @Test
    fun `send event after saving a new Employer`() {
      wheneverCreateOutboundEvent()

      employerCreator.create(createEmployerRequest)

      val eventCaptor = argumentCaptor<OutboundEvent>()
      verify(outboundEventsService).handleMessage(eventCaptor.capture())
      val actualEvent = eventCaptor.firstValue

      assertThat(actualEvent.eventType).isEqualTo(EmployerEventType.CREATED.eventTypeCode)
      assertThat(actualEvent.content).isNotBlank()
      val payloadJson = objectMapper.readTree(actualEvent.content)
      assertThat(payloadJson.get("employerId").textValue()).isEqualTo(createEmployerRequest.id)
    }

    @Test
    fun `send event after saving an existing Employer`() {
      givenEmployerCreated()

      employerCreator.update(createEmployerRequest)

      val eventCaptor = argumentCaptor<OutboundEvent>()
      verify(outboundEventsService).handleMessage(eventCaptor.capture())
      val actualEvent = eventCaptor.firstValue

      assertThat(actualEvent.eventType).isEqualTo(EmployerEventType.UPDATED.eventTypeCode)
      assertThat(actualEvent.content).isNotBlank()
      val payloadJson = objectMapper.readTree(actualEvent.content)
      assertThat(payloadJson.get("employerId").textValue()).isEqualTo(createEmployerRequest.id)
    }
  }

  private fun wheneverCreateOutboundEvent() {
    whenever(uuidGenerator.generate()).thenReturn(UUID.randomUUID().toString())
    whenever(timeProvider.nowAsInstant()).thenReturn(Instant.now())
  }

  private fun givenEmployerCreated() {
    wheneverCreateOutboundEvent()
    val employer = Employer(
      id = EntityId(createEmployerRequest.id),
      name = createEmployerRequest.name,
      description = createEmployerRequest.description,
      sector = createEmployerRequest.sector,
      status = createEmployerRequest.status,
    )
    whenever(employerRepository.findById(employer.id)).thenReturn(Optional.of(employer))
  }
}
