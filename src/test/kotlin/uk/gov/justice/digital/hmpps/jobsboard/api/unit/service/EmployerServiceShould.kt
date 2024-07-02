package uk.gov.justice.digital.hmpps.jobsboard.api.unit.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateEmployerRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.service.EmployerService
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class EmployerServiceShould {

  @Mock
  private lateinit var employerRepository: EmployerRepository

  @InjectMocks
  private lateinit var jobEmployerService: EmployerService

  private lateinit var employer: Employer

  @BeforeEach
  fun setUp() {
    employer =
      Employer(
        id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
        name = "Sainsbury's",
        description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
        createdBy = "Sacintha",
        createdWhen = LocalDateTime.parse("2024-05-16T11:15:04.915205"),
        modifiedBy = "Javier",
        modifiedWhen = LocalDateTime.parse("2024-05-16T11:16:04.915205"),
        sector = "sector",
        status = "status",
      )
  }

  @Test
  fun `create a valid Employer`() {
    val createEmployerRequest = CreateEmployerRequest.from(
      id = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
      name = "Sainsbury's",
      description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
      createdBy = "Sacintha",
      createdWhen = LocalDateTime.parse("2024-05-16T11:15:04.915205"),
      modifiedBy = "Javier",
      modifiedWhen = LocalDateTime.parse("2024-05-16T11:16:04.915205"),
      sector = "sector",
      status = "status",
    )

    jobEmployerService.createEmployer(createEmployerRequest)

    verify(employerRepository).save(employer)
  }

  @Test
  fun `retrieve an Employer when found`() {
    `when`(employerRepository.findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))).thenReturn(Optional.of(employer))

    val employer: Employer = jobEmployerService.retrieveEmployer("1db79c55-cc88-4a1d-94fa-7a21c590c713")

    assertEquals(this.employer, employer)
    verify(employerRepository, times(1)).findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))
  }

  @Test
  fun `throw an Exception when an Employer is not found`() {
    `when`(employerRepository.findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))).thenReturn(Optional.empty())

    val exception = assertThrows<RuntimeException> {
      jobEmployerService.retrieveEmployer("39683af0-eb4c-4fd4-b6a5-34a26d6b9039")
    }

    assertEquals("Employer not found", exception.message)
    verify(employerRepository, times(1)).findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))
  }
}
