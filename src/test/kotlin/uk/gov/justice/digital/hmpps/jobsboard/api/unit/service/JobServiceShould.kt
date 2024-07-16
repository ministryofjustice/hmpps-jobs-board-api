package uk.gov.justice.digital.hmpps.jobsboard.api.unit.service

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork.BEAUTY
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateJobRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.service.JobService
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider
import java.time.LocalDateTime
import java.time.Month.JULY
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class JobServiceShould {

  @Mock
  private lateinit var jobRepository: JobRepository

  @Mock
  private lateinit var employerRepository: EmployerRepository
  private val timeProvider: TimeProvider = mock(TimeProvider::class.java)

  @InjectMocks
  private lateinit var jobService: JobService

  private val fixedTime = LocalDateTime.of(2024, JULY, 20, 22, 6)
  private val expectedEmployer = Employer(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century",
    sector = "sector",
    status = "status",
    createdAt = fixedTime,
  )
  private val expectedjob = Job(
    id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
    employer = expectedEmployer,
    salaryPeriodName = SalaryPeriod.PER_DAY,
    workPatternName = ContractHours.JOB_SHARE,
    hoursName = Hours.ZERO_HOURS,
    additionalSalaryInformation = "Salary will be credited at the end of each shift",
    desirableJobCriteria = "Candidate should be proactive and prgmatic",
    essentialJobCriteria = "Need some one with techincal abilities at a beginer level in computer",
    closingDate = LocalDateTime.now().plusDays(20),
    howToApply = "Apply through Website",
    jobTitle = "Java developer",
    createdBy = "Sacintha",
    createdDateTime = LocalDateTime.now(),
    postingDate = LocalDateTime.now().plusDays(5).toString(),
    deletedBy = "Sacintha",
    deletedDateTime = LocalDateTime.now().plusDays(5),
    modifiedBy = "Sacintha",
    modifiedDateTime = LocalDateTime.now(),
    nationalMinimumWage = true,
    postCode = "EH7 5HH",
    city = "Edinburgh",
    ringFencedJob = true,
    rollingJobOppurtunity = true,
    activeJob = true,
    deletedJob = false,
    salaryFrom = "10£",
    salaryTo = "15£",
    typeOfWork = BEAUTY,
    distance = 0,
  )
  private val createJobRequest = CreateJobRequest.from(
    expectedjob,
  )

  @BeforeEach
  fun setUp() {
    `when`(timeProvider.now()).thenReturn(fixedTime)
  }

  @Test
  fun `create an job with valid details`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"))).thenReturn(Optional.of(expectedEmployer))

    jobService.save(createJobRequest)

    val jobCaptor = argumentCaptor<Job>()
    verify(jobRepository).save(jobCaptor.capture())
    val actualjob = jobCaptor.firstValue

    assertEquals(expectedjob.id, actualjob.id)
    assertEquals(expectedjob.employer?.name, actualjob.employer?.name)
    assertEquals(expectedjob.jobTitle, actualjob.jobTitle)
  }

  @Test
  fun `create an job with current time`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"))).thenReturn(Optional.of(expectedEmployer))

    jobService.save(createJobRequest)

    val jobCaptor = argumentCaptor<Job>()
    verify(jobRepository).save(jobCaptor.capture())
    val actualjob = jobCaptor.firstValue

    assertEquals(expectedjob.createdDateTime, actualjob.createdDateTime)
  }

  @Test
  fun `throw exception for invalid UUID`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"))).thenReturn(Optional.of(expectedEmployer))

    val createJobRequest = CreateJobRequest(
      id = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c_invalid",
      employerId = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
      salaryPeriodName = SalaryPeriod.PER_DAY,
      workPatternName = ContractHours.JOB_SHARE,
      hoursName = Hours.ZERO_HOURS,
      additionalSalaryInformation = "Salary will be credited at the end of each shift",
      desirableJobCriteria = "Candidate should be proactive and prgmatic",
      essentialJobCriteria = "Need some one with techincal abilities at a beginer level in computer",
      closingDate = LocalDateTime.now().plusDays(20),
      howToApply = "Apply through Website",
      jobTitle = "Java developer",
      createdBy = "Sacintha",
      createdDateTime = LocalDateTime.now(),
      postingDate = LocalDateTime.now().plusDays(5).toString(),
      deletedBy = "Sacintha",
      deletedDateTime = LocalDateTime.now().plusDays(5),
      modifiedBy = "Sacintha",
      modifiedDateTime = LocalDateTime.now(),
      nationalMinimumWage = true,
      postCode = "EH7 5HH",
      city = "Edinburgh",
      ringFencedJob = true,
      rollingJobOppurtunity = true,
      activeJob = true,
      deletedJob = false,
      salaryFrom = "10£",
      salaryTo = "15£",
      typeOfWork = BEAUTY,
    )

    val exception = assertThrows<IllegalArgumentException> {
      jobService.save(createJobRequest)
    }

    assertEquals("Invalid UUID format: {${createJobRequest.id}}", exception.message)
    verify(jobRepository, never()).save(any(Job::class.java))
  }

  @Test
  fun `throw exception for empty UUID`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"))).thenReturn(Optional.of(expectedEmployer))

    val createJobRequest = CreateJobRequest(
      id = "",
      employerId = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
      salaryPeriodName = SalaryPeriod.PER_DAY,
      workPatternName = ContractHours.JOB_SHARE,
      hoursName = Hours.ZERO_HOURS,
      additionalSalaryInformation = "Salary will be credited at the end of each shift",
      desirableJobCriteria = "Candidate should be proactive and prgmatic",
      essentialJobCriteria = "Need some one with techincal abilities at a beginer level in computer",
      closingDate = LocalDateTime.now().plusDays(20),
      howToApply = "Apply through Website",
      jobTitle = "Java developer",
      createdBy = "Sacintha",
      createdDateTime = LocalDateTime.now(),
      postingDate = LocalDateTime.now().plusDays(5).toString(),
      deletedBy = "Sacintha",
      deletedDateTime = LocalDateTime.now().plusDays(5),
      modifiedBy = "Sacintha",
      modifiedDateTime = LocalDateTime.now(),
      nationalMinimumWage = true,
      postCode = "EH7 5HH",
      city = "Edinburgh",
      ringFencedJob = true,
      rollingJobOppurtunity = true,
      activeJob = true,
      deletedJob = false,
      salaryFrom = "10£",
      salaryTo = "15£",
      typeOfWork = BEAUTY,
    )

    val exception = assertThrows<IllegalArgumentException> {
      jobService.save(createJobRequest)
    }

    assertEquals("EntityId cannot be empty", exception.message)
    verify(jobRepository, never()).save(any(Job::class.java))
  }

  @Test
  fun `throw exception for null UUID`() {
    whenever(employerRepository.findById(EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"))).thenReturn(Optional.of(expectedEmployer))

    val createJobRequest = CreateJobRequest(
      id = "00000000-0000-0000-0000-00000",
      employerId = "eaf7e96e-e45f-461d-bbcb-fd4cedf0499c",
      salaryPeriodName = SalaryPeriod.PER_DAY,
      workPatternName = ContractHours.JOB_SHARE,
      hoursName = Hours.ZERO_HOURS,
      additionalSalaryInformation = "Salary will be credited at the end of each shift",
      desirableJobCriteria = "Candidate should be proactive and prgmatic",
      essentialJobCriteria = "Need some one with techincal abilities at a beginer level in computer",
      closingDate = LocalDateTime.now().plusDays(20),
      howToApply = "Apply through Website",
      jobTitle = "Java developer",
      createdBy = "Sacintha",
      createdDateTime = LocalDateTime.now(),
      postingDate = LocalDateTime.now().plusDays(5).toString(),
      deletedBy = "Sacintha",
      deletedDateTime = LocalDateTime.now().plusDays(5),
      modifiedBy = "Sacintha",
      modifiedDateTime = LocalDateTime.now(),
      nationalMinimumWage = true,
      postCode = "EH7 5HH",
      city = "Edinburgh",
      ringFencedJob = true,
      rollingJobOppurtunity = true,
      activeJob = true,
      deletedJob = false,
      salaryFrom = "10£",
      salaryTo = "15£",
      typeOfWork = BEAUTY,
    )

    val exception = assertThrows<IllegalArgumentException> {
      jobService.save(createJobRequest)
    }

    assertEquals("EntityId cannot be null: {${createJobRequest.id}}", exception.message)
    verify(jobRepository, never()).save(any(Job::class.java))
  }

  @Test
  fun `return true when job exists`() {
    val jobId = UUID.randomUUID().toString()
    whenever(jobRepository.existsById(EntityId(jobId))).thenReturn(true)

    assertTrue(jobService.existsById(jobId))
  }

  @Test
  fun `return false when job does not exist`() {
    val jobId = UUID.randomUUID().toString()
    whenever(jobRepository.existsById(EntityId(jobId))).thenReturn(false)

    assertFalse(jobService.existsById(jobId))
  }

  @Test
  fun `retrieve an Employer when found`() {
    `when`(jobRepository.findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))).thenReturn(
      Optional.of(
        expectedjob,
      ),
    )

    val actualjob = jobService.retrieve("1db79c55-cc88-4a1d-94fa-7a21c590c713")

    assertEquals(expectedjob, actualjob)
    verify(jobRepository, times(1)).findById(EntityId("1db79c55-cc88-4a1d-94fa-7a21c590c713"))
  }

  @Test
  fun `throw an Exception when an Job is not found`() {
    `when`(jobRepository.findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))).thenReturn(Optional.empty())

    val exception = assertThrows<RuntimeException> {
      jobService.retrieve("39683af0-eb4c-4fd4-b6a5-34a26d6b9039")
    }

    assertEquals("Job not found", exception.message)
    verify(jobRepository, times(1)).findById(EntityId("39683af0-eb4c-4fd4-b6a5-34a26d6b9039"))
  }
}
