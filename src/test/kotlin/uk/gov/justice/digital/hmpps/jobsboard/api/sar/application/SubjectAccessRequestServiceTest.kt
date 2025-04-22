package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.springframework.data.history.Revision
import org.springframework.data.history.Revisions
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationHistoryRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARFilter
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubjectAccessRequestServiceTest {

  private lateinit var applicationRepository: ApplicationRepository
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository
  private lateinit var archivedRepository: ArchivedRepository
  private lateinit var applicationHistoryRetriever: ApplicationHistoryRetriever
  private lateinit var service: SubjectAccessRequestService
  val prisonerNumber = "A1234BC"
  private var sarFilter: SARFilter = SARFilter(
    prn = prisonerNumber,
    fromDate = null,
    toDate = null,
  )

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

    every {
      applicationRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    } returns applications
    every { applicationHistoryRetriever.retrieveAllApplicationHistories(any(), any()) } returns mockRevisions
    val result = service.fetchApplications(sarFilter).get()
    assertEquals(1, result.size)
    assertEquals("Delivery Driver", result[0]!!.jobTitle)
    assertEquals("Amazon Flex", result[0]!!.employerName)
    verify {
      applicationRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    }
  }

  @Test
  fun `should return formatted ApplicationDTO list when applications exist`() {
    val createdAt0 = OffsetDateTime.parse("2024-11-15T10:00:00Z")
    val lastModifiedAt0 = OffsetDateTime.parse("2024-12-15T10:00:00Z")

    val employer1 = mockk<Employer> {
      every { name } returns "The AA"
    }

    val entity1 = mockk<EntityId> {
      every { id } returns "1"
    }

    val applicationList = listOf(
      Application(
        id = mockk {
          every { id } returns "1"
        },
        prisonId = "MDI",
        prisonNumber = prisonerNumber,
        firstName = "Stephen",
        lastName = "James",
        status = "APPLICATION_MADE",
        additionalInformation = "",
        job = mockk<Job> {
          every { title } returns "Car mechanic"
          every { employer } returns employer1
          every { id } returns entity1
        },
      ),
    )

    applicationList.get(0).createdAt = createdAt0.toInstant()
    applicationList.get(0).lastModifiedAt = lastModifiedAt0.toInstant()

    every {
      applicationRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    } returns applicationList
    every {
      applicationHistoryRetriever.retrieveAllApplicationHistories(any(), any())
    } returns null

    val result = service.fetchApplications(sarFilter).get()

    assertEquals(1, result.size)
    assertEquals(result[0]!!.jobTitle, "Car mechanic")
    assertEquals(result[0]!!.createdAt, "2024-11-15")
    assertEquals(result[0]!!.lastModifiedAt, "2024-12-15")
  }

  @Test
  fun `should return empty list when no applications exist`() {
    every {
      applicationRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    } returns emptyList()
    val result = service.fetchApplications(sarFilter).get()
    assertTrue(result.isEmpty())
    verify {
      applicationRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    }
  }

  @Test
  fun `should return list of ExpressionOfInterestDTO when expressions exist`() {
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

    every {
      expressionOfInterestRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    } returns expressions
    val result = service.fetchExpressionsOfInterest(sarFilter).get()
    assertEquals(1, result.size)
    assertEquals("Car mechanic", result[0].jobTitle)
    assertEquals("The AA", result[0].employerName)
    verify {
      expressionOfInterestRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    }
  }

  @Test
  fun `should return empty list when no expressions of interest exist`() {
    every {
      expressionOfInterestRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    } returns emptyList()

    val result = service.fetchExpressionsOfInterest(sarFilter).get()

    assertTrue(result.isEmpty())
    verify {
      expressionOfInterestRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    }
  }

  @Test
  fun `should return list of ArchivedDTO when archived jobs exist`() {
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

    every {
      archivedRepository.findByPrisonNumberAndDateBetween(
        prisonerNumber,
        fromDate = null,
        toDate = null,
      )
    } returns archivedJobs

    val result = service.fetchArchivedJobs(sarFilter).get()

    assertEquals(1, result.size)
    assertEquals("Sales person", result[0].jobTitle)
    assertEquals("Boots", result[0].employerName)
    verify {
      archivedRepository.findByPrisonNumberAndDateBetween(
        prisonerNumber,
        fromDate = null,
        toDate = null,
      )
    }
  }

  @Test
  fun `should return empty list of ArchivedDTO when archived jobs exist and fromDate is later than created`() {
    val testFromDate = Instant.parse("2024-03-06T00:00:00Z")
    val testSarFilter: SARFilter = SARFilter(
      prn = prisonerNumber,
      fromDate = testFromDate,
      toDate = null,
    )

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

    every {
      archivedRepository.findByPrisonNumberAndDateBetween(
        testSarFilter.prn,
        testSarFilter.fromDate,
        testSarFilter.toDate,
      )
    } returns emptyList()

    val result = service.fetchArchivedJobs(testSarFilter).get()

    assertEquals(0, result.size)
    verify {
      archivedRepository.findByPrisonNumberAndDateBetween(
        testSarFilter.prn,
        testSarFilter.fromDate,
        testSarFilter.toDate,
      )
    }
  }

  @Test
  fun `should return list of ArchivedDTO when archived jobs exist and toDate is earlier than created`() {
    val testToDate = Instant.parse("2024-03-06T00:00:00Z")
    val testSarFilter: SARFilter = SARFilter(
      prn = prisonerNumber,
      fromDate = null,
      toDate = testToDate,
    )

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

    every {
      archivedRepository.findByPrisonNumberAndDateBetween(
        testSarFilter.prn,
        testSarFilter.fromDate,
        testSarFilter.toDate,
      )
    } returns archivedJobs

    val result = service.fetchArchivedJobs(testSarFilter).get()

    assertEquals(1, result.size)
    verify {
      archivedRepository.findByPrisonNumberAndDateBetween(
        testSarFilter.prn,
        testSarFilter.fromDate,
        testSarFilter.toDate,
      )
    }
  }

  @Test
  fun `should return empty list when no archived jobs exist`() {
    every {
      archivedRepository.findByPrisonNumberAndDateBetween(
        eq(sarFilter.prn),
        eq(sarFilter.fromDate),
        eq(sarFilter.toDate),
      )
    } returns emptyList()

    val result = service.fetchArchivedJobs(sarFilter).get()

    assertTrue(result.isEmpty())
    verify {
      archivedRepository.findByPrisonNumberAndDateBetween(
        sarFilter.prn,
        sarFilter.fromDate,
        sarFilter.toDate,
      )
    }
  }
}
