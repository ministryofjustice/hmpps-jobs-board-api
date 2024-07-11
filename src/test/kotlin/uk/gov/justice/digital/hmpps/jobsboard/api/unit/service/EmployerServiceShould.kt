package uk.gov.justice.digital.hmpps.jobsboard.api.unit.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.service.EmployerService
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider
import java.time.LocalDateTime
import java.time.Month.JULY
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class EmployerServiceShould {

  @Mock
  private lateinit var employerRepository: EmployerRepository
  private val timeProvider: TimeProvider = mock(TimeProvider::class.java)

  @InjectMocks
  private lateinit var employerService: EmployerService

  private val fixedTime = LocalDateTime.of(2024, JULY, 20, 22, 6)
  private val tescoEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Tesco",
    description = "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
    sector = "RETAIL",
    status = "SILVER",
    createdAt = fixedTime,
  )

  private val sainsburysEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
    sector = "RETAIL",
    status = "GOLD",
    createdAt = fixedTime,
  )

  private val expectedEmployer = sainsburysEmployer
  private val createEmployerRequest = CreateEmployerRequest.from(
    id = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
    sector = "RETAIL",
    status = "GOLD",
  )

  @BeforeEach
  fun setUp() {
    `when`(timeProvider.now()).thenReturn(fixedTime)
  }

  @Test
  fun `save an Employer with valid details`() {
    employerService.save(createEmployerRequest)

    val employerCaptor = argumentCaptor<Employer>()
    verify(employerRepository).save(employerCaptor.capture())
    val actualEmployer = employerCaptor.firstValue

    assertThat(expectedEmployer.id).isEqualTo(actualEmployer.id)
    assertThat(expectedEmployer.name).isEqualTo(actualEmployer.name)
    assertThat(expectedEmployer.description).isEqualTo(actualEmployer.description)
    assertThat(expectedEmployer.sector).isEqualTo(actualEmployer.sector)
    assertThat(expectedEmployer.status).isEqualTo(actualEmployer.status)
  }

  @Test
  fun `save an Employer with current time`() {
    employerService.save(createEmployerRequest)

    val employerCaptor = argumentCaptor<Employer>()
    verify(employerRepository).save(employerCaptor.capture())
    val actualEmployer = employerCaptor.firstValue

    assertThat(expectedEmployer.createdAt).isEqualTo(actualEmployer.createdAt)
  }

  @Test
  fun `throw exception for invalid UUID`() {
    val createEmployerRequest = CreateEmployerRequest.from(
      id = "invalid-uuid",
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      sector = "sector",
      status = "status",
    )

    val exception = assertThrows<IllegalArgumentException> {
      employerService.save(createEmployerRequest)
    }

    assertThat("Invalid UUID format: {${createEmployerRequest.id}}").isEqualTo(exception.message)
    verify(employerRepository, never()).save(any(Employer::class.java))
  }

  @Test
  fun `throw exception for empty UUID`() {
    val createEmployerRequest = CreateEmployerRequest.from(
      id = "",
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      sector = "sector",
      status = "status",
    )

    val exception = assertThrows<IllegalArgumentException> {
      employerService.save(createEmployerRequest)
    }

    assertThat("EntityId cannot be empty").isEqualTo(exception.message)
    verify(employerRepository, never()).save(any(Employer::class.java))
  }

  @Test
  fun `throw exception for null UUID`() {
    val createEmployerRequest = CreateEmployerRequest.from(
      id = "00000000-0000-0000-0000-00000",
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      sector = "sector",
      status = "status",
    )

    val exception = assertThrows<IllegalArgumentException> {
      employerService.save(createEmployerRequest)
    }

    assertThat("EntityId cannot be null: {${createEmployerRequest.id}}").isEqualTo(exception.message)
    verify(employerRepository, never()).save(any(Employer::class.java))
  }

  @Test
  fun `return true when employer exists`() {
    val employerId = UUID.randomUUID().toString()
    whenever(employerRepository.existsById(EntityId(employerId))).thenReturn(true)

    assertThat(employerService.existsById(employerId)).isTrue()
  }

  @Test
  fun `return false when employer does not exist`() {
    val employerId = UUID.randomUUID().toString()
    whenever(employerRepository.existsById(EntityId(employerId))).thenReturn(false)

    assertThat(employerService.existsById(employerId)).isFalse()
  }

  @Test
  fun `return an Employer when found`() {
    `when`(employerRepository.findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))).thenReturn(
      Optional.of(
        expectedEmployer,
      ),
    )

    val actualEmployer: Employer = employerService.retrieve("1db79c55-cc88-4a1d-94fa-7a21c590c713")

    assertEquals(expectedEmployer, actualEmployer)
    verify(employerRepository, times(1)).findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))
  }

  @Test
  fun `throw an Exception when an Employer is not found`() {
    `when`(employerRepository.findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))).thenReturn(Optional.empty())

    val exception = assertThrows<RuntimeException> {
      employerService.retrieve("39683af0-eb4c-4fd4-b6a5-34a26d6b9039")
    }

    assertEquals("Employer not found", exception.message)
    verify(employerRepository, times(1)).findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))
  }

  @Test
  fun `return all employers`() {
    val employers = listOf(tescoEmployer, sainsburysEmployer)
    whenever(employerRepository.findAll()).thenReturn(employers)

    val result = employerService.getEmployers()

    assertThat(result).hasSize(2)
    assertThat(result).isEqualTo(employers)
  }
}
