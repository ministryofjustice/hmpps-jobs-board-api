package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.assertj.core.api.Assertions.assertThat
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
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.TestBase
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
    employerCreator.createOrUpdate(createEmployerRequest)

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
    employerCreator.createOrUpdate(createEmployerRequest)

    val employerCaptor = argumentCaptor<Employer>()
    verify(employerRepository).save(employerCaptor.capture())
    val actualEmployer = employerCaptor.firstValue

    assertThat(actualEmployer.createdAt).isEqualTo(expectedEmployer.createdAt)
    assertThat(actualEmployer.modifiedAt).isEqualTo(expectedEmployer.modifiedAt)
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
      employerCreator.createOrUpdate(createEmployerRequest)
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
      employerCreator.createOrUpdate(createEmployerRequest)
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
      employerCreator.createOrUpdate(createEmployerRequest)
    }

    verify(employerRepository, never()).save(any(Employer::class.java))
    assertThat(exception.message).isEqualTo("EntityId cannot be null: {${createEmployerRequest.id}}")
  }
}
