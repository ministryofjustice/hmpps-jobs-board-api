package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider
import java.time.LocalDateTime
import java.time.Month.JULY
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class EmployerRetrieverShould {

  @Mock
  private lateinit var employerRepository: EmployerRepository
  private val timeProvider: TimeProvider = mock(TimeProvider::class.java)

  @InjectMocks
  private lateinit var employerRetriever: EmployerRetriever

  private val fixedTime = LocalDateTime.of(2024, JULY, 20, 22, 6)
  private val tescoEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Tesco",
    description = "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
    sector = "RETAIL",
    status = "SILVER",
  )

  private val sainsburysEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
    sector = "RETAIL",
    status = "GOLD",
  )

  private val expectedEmployer = sainsburysEmployer

  @BeforeEach
  fun setUp() {
    `when`(timeProvider.now()).thenReturn(fixedTime)
  }

  @Test
  fun `return an Employer when found`() {
    `when`(employerRepository.findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))).thenReturn(
      Optional.of(
        expectedEmployer,
      ),
    )

    val actualEmployer: Employer = employerRetriever.retrieve("1db79c55-cc88-4a1d-94fa-7a21c590c713")

    verify(employerRepository, times(1)).findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))
    assertEquals(expectedEmployer, actualEmployer)
  }

  @Test
  fun `throw an Exception when an Employer is not found`() {
    `when`(employerRepository.findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))).thenReturn(Optional.empty())

    val exception = assertThrows<RuntimeException> {
      employerRetriever.retrieve("39683af0-eb4c-4fd4-b6a5-34a26d6b9039")
    }

    verify(employerRepository, times(1)).findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))
    assertEquals("Employer not found", exception.message)
  }

  @Test
  fun `return a paginated list of all employers when filters are null`() {
    val employers = listOf(tescoEmployer, sainsburysEmployer)
    val name = null
    val sector = null
    val pageNumber = 0
    val pageSize = 2
    val pageable = Pageable.ofSize(pageSize).withPage(pageNumber)
    val pagedResult: Page<Employer> = PageImpl(employers, pageable, employers.size.toLong())
    whenever(employerRepository.findAll(pageable)).thenReturn(pagedResult)

    val result = employerRetriever.retrieveAllEmployers(name, sector, pageable)

    verify(employerRepository, times(1)).findAll(pageable)
    assertThat(result.content).isEqualTo(pagedResult.content)
  }

  @Test
  fun `return a paginated list of employers filtered by name`() {
    val employers = listOf(tescoEmployer)
    val name = "Tesco"
    val sector = null
    val pageNumber = 0
    val pageSize = 10
    val pageable = Pageable.ofSize(pageSize).withPage(pageNumber)
    val pagedResult: Page<Employer> = PageImpl(employers, pageable, employers.size.toLong())
    whenever(employerRepository.findByNameIgnoringCaseContaining(name, pageable)).thenReturn(pagedResult)

    val result = employerRetriever.retrieveAllEmployers(name, sector, pageable)

    verify(employerRepository, times(1)).findByNameIgnoringCaseContaining(name, pageable)
    assertThat(result.content).isEqualTo(pagedResult.content)
  }

  @Test
  fun `return a paginated list of employers filtered by sector`() {
    val employers = listOf(tescoEmployer)
    val name = null
    val sector = "RETAIL"
    val pageNumber = 0
    val pageSize = 10
    val pageable = Pageable.ofSize(pageSize).withPage(pageNumber)
    val pagedResult: Page<Employer> = PageImpl(employers, pageable, employers.size.toLong())
    whenever(employerRepository.findBySectorIgnoringCase(sector, pageable)).thenReturn(pagedResult)

    val result = employerRetriever.retrieveAllEmployers(name, sector, pageable)

    verify(employerRepository, times(1)).findBySectorIgnoringCase(sector, pageable)
    assertThat(result.content).isEqualTo(pagedResult.content)
  }

  @Test
  fun `return a paginated list of employers filtered by name AND sector`() {
    val employers = listOf(tescoEmployer)
    val name = "Tesco"
    val sector = "RETAIL"
    val pageNumber = 0
    val pageSize = 10
    val pageable = Pageable.ofSize(pageSize).withPage(pageNumber)
    val pagedResult: Page<Employer> = PageImpl(employers, pageable, employers.size.toLong())
    whenever(employerRepository.findByNameContainingAndSectorAllIgnoringCase(name, sector, pageable)).thenReturn(pagedResult)

    val result = employerRetriever.retrieveAllEmployers(name, sector, pageable)

    verify(employerRepository, times(1)).findByNameContainingAndSectorAllIgnoringCase(name, sector, pageable)
    assertThat(result.content).isEqualTo(pagedResult.content)
  }
}
