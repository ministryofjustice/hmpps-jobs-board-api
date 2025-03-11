package uk.gov.justice.digital.hmpps.jobsboard.api.subjectAccessRequest.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.history.Revision
import org.springframework.data.history.Revisions
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationHistoryRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.application.SubjectAccessRequestService
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubjectAccessRequestServiceTest {

  private lateinit var applicationRepository: ApplicationRepository
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository
  private lateinit var archivedRepository: ArchivedRepository
  private lateinit var applicationHistoryRetriever: ApplicationHistoryRetriever
  private lateinit var service: SubjectAccessRequestService

  @BeforeEach
  fun setUp() {
    applicationRepository = mockk()
    expressionOfInterestRepository = mockk()
    archivedRepository = mockk()
    applicationHistoryRetriever = mockk()
    service = SubjectAccessRequestService(
      applicationRepository,
      expressionOfInterestRepository,
      archivedRepository,
      applicationHistoryRetriever,
    )
  }

  @Test
  fun `should return list of ApplicationDTO when applications exist`() {
    val prisonNumber = "A1234BC"
    val prisonCentreId = "C12"
    val mockApplication = mockk<Application>(relaxed = true) {
      every { firstName } returns "Stephen"
      every { lastName } returns "Jones"
      every { status } returns "APPLICATION_MADE"
      every { prisonId } returns prisonCentreId
      every { lastModifiedAt } returns Instant.parse("2024-11-25T09:45:29.916505Z")
      every { createdAt } returns Instant.parse("2023-10-25T09:45:29.916505Z")
      every { job.title } returns "Delivery Driver"
      every { job.employer.name } returns "Amazon Flex"
      every { job.id } returns EntityId(UUID.randomUUID().toString())
    }

    val applications = listOf(
      mockApplication,
    )

    val mockRevision = mockk<Revision<Long, Application>>(relaxed = true) {
      every { entity } returns mockApplication
    }

    val mockRevisions = Revisions.of(listOf(mockRevision))

    every { applicationRepository.findByPrisonNumber(prisonNumber) } returns applications
    every { applicationHistoryRetriever.retrieveAllApplicationHistories(any(), any()) } returns mockRevisions
    val result = service.fetchApplications(prisonNumber).get()
    assertEquals(1, result.size)
    assertEquals("Delivery Driver", result[0].jobTitle)
    assertEquals("Amazon Flex", result[0].employerName)
    verify { applicationRepository.findByPrisonNumber(prisonNumber) }
  }

  @Test
  fun `should return empty list when no applications exist`() {
    val prisonNumber = "A1234BC"
    every { applicationRepository.findByPrisonNumber(prisonNumber) } returns emptyList()
    val result = service.fetchApplications(prisonNumber).get()
    assertTrue(result.isEmpty())
    verify { applicationRepository.findByPrisonNumber(prisonNumber) }
  }

  @Test
  fun `should return list of ExpressionOfInterestDTO when expressions exist`() {
    val prisonNumber = "A1234BC"
    val createdAt: Instant = Instant.parse("2023-03-06T00:00:00Z")
    val expressions = listOf(
      ExpressionOfInterest(
        job = mockk {
          every { title } returns "Car mechanic"
          every { employer.name } returns "The AA"
        },
        createdAt = createdAt,
        id = mockk(),
      ),
    )

    every { expressionOfInterestRepository.findByIdPrisonNumber(prisonNumber) } returns expressions
    val result = service.fetchExpressionsOfInterest(prisonNumber).get()
    assertEquals(1, result.size)
    assertEquals("Car mechanic", result[0].jobTitle)
    assertEquals("The AA", result[0].employerName)
    assertEquals(createdAt.toString(), result[0].createdAt)
    verify { expressionOfInterestRepository.findByIdPrisonNumber(prisonNumber) }
  }

  @Test
  fun `should return empty list when no expressions of interest exist`() {
    val prisonNumber = "A1234BC"
    every { expressionOfInterestRepository.findByIdPrisonNumber(prisonNumber) } returns emptyList()

    val result = service.fetchExpressionsOfInterest(prisonNumber).get()

    assertTrue(result.isEmpty())
    verify { expressionOfInterestRepository.findByIdPrisonNumber(prisonNumber) }
  }

  @Test
  fun `should return list of ArchivedDTO when archived jobs exist`() {
    val prisonNumber = "A1234BC"
    val createdAt = Instant.parse("2023-03-06T00:00:00Z")
    val archivedJobs = listOf(
      Archived(
        job = mockk {
          every { title } returns "Sales person"
          every { employer.name } returns "Boots"
        },
        createdAt = createdAt,
        id = mockk(),
      ),
    )

    every { archivedRepository.findByIdPrisonNumber(prisonNumber) } returns archivedJobs

    val result = service.fetchArchivedJobs(prisonNumber).get()

    assertEquals(1, result.size)
    assertEquals("Sales person", result[0].jobTitle)
    assertEquals("Boots", result[0].employerName)
    assertEquals(createdAt.toString(), result[0].createdAt)
    verify { archivedRepository.findByIdPrisonNumber(prisonNumber) }
  }

  @Test
  fun `should return empty list when no archived jobs exist`() {
    val prisonNumber = "A1234BC"
    every { archivedRepository.findByIdPrisonNumber(prisonNumber) } returns emptyList()

    val result = service.fetchArchivedJobs(prisonNumber).get()

    assertTrue(result.isEmpty())
    verify { archivedRepository.findByIdPrisonNumber(prisonNumber) }
  }
}
