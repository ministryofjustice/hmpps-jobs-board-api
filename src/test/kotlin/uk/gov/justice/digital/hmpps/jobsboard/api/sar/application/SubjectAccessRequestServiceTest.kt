package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

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
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARFilter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubjectAccessRequestServiceTest {

  private lateinit var applicationRepository: ApplicationRepository
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository
  private lateinit var archivedRepository: ArchivedRepository
  private lateinit var applicationHistoryRetriever: ApplicationHistoryRetriever
  private lateinit var service: SubjectAccessRequestService
  private var prisonerNumber = "A1234BC"
  private var sarFilter: SARFilter = SARFilter(
    prn = prisonerNumber,
    fromDate = null,
    toDate = null,
  )
  private val atEndOfDay = LocalTime.MAX.truncatedTo(ChronoUnit.MICROS)

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
  fun `should return empty list of applications created date is later than end date`() {
    val createdAtTime = Instant.parse("2024-12-31T23:59:59.999999Z")
    val modifiedAtTime = Instant.parse("2025-01-31T23:59:59.999999Z")
    val testSarFilter = SARFilter(
      prn = prisonerNumber,
      toDate = OffsetDateTime.parse("2024-10-31T00:00:00Z").toLocalDate(),
      fromDate = null,
    )
    val applicationId = EntityId(UUID.randomUUID().toString()).id

    val mockApplication = mockk<Application>(relaxed = true) {
      every { job.title } returns "Cleaner"
      every { job.employer.name } returns "Tesco"
      every { id.id } returns applicationId.toString()
      every { createdAt } returns createdAtTime
      every { lastModifiedAt } returns createdAtTime
    }

    val revisedApplication = mockk<Application>(relaxed = true) {
      every { job.title } returns "Cleaner"
      every { job.employer.name } returns "Tesco"
      every { id.id } returns applicationId
      every { createdAt } returns createdAtTime
      every { lastModifiedAt } returns modifiedAtTime
    }

    val mockRevision = mockk<Revision<Long, Application>> {
      every { entity } returns revisedApplication
    }

    every {
      applicationRepository.findByPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    } returns emptyList()

    every {
      applicationHistoryRetriever.retrieveAllApplicationHistories(applicationId)
    } returns Revisions.none()

    val result = service.fetchApplications(testSarFilter).get()

    assertEquals(0, result.size)

    verify {
      applicationRepository.findByPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    }
  }

  @Test
  fun `should return list of applications filterd by date when end date is provided and all related application histories`() {
    val createdAtTime = Instant.parse("2024-11-29T23:59:59.999999Z")
    val modifiedAtTime = Instant.parse("2025-01-31T23:59:59.999999Z")
    val testSarFilter = SARFilter(
      prn = prisonerNumber,
      toDate = OffsetDateTime.parse("2024-12-31T00:00:00Z").toLocalDate(),
      fromDate = null,
    )
    val applicationId = EntityId(UUID.randomUUID().toString()).id

    val mockApplication = mockk<Application>(relaxed = true) {
      every { job.title } returns "Cleaner"
      every { job.employer.name } returns "Tesco"
      every { id.id } returns applicationId.toString()
      every { createdAt } returns createdAtTime
      every { lastModifiedAt } returns createdAtTime
    }

    val revisedApplication = mockk<Application>(relaxed = true) {
      every { firstName } returns "Rachel"
      every { lastName } returns "Smith"
      every { status } returns "SELECTED_FOR_INTERVIEW"
      every { prisonId } returns "MOI"
      every { lastModifiedAt } returns modifiedAtTime
    }

    val mockRevision = mockk<Revision<Long, Application>> {
      every { entity } returns revisedApplication
    }

    every {
      applicationRepository.findByPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    } returns listOf(mockApplication)

    every {
      applicationHistoryRetriever.retrieveAllApplicationHistories(applicationId)
    } returns Revisions.of(listOf(mockRevision))

    val result = service.fetchApplications(testSarFilter).get()

    val applicationDt0 = result[0]

    assertEquals(1, result.size)
    assertEquals(1, applicationDt0.histories.size)
    assertEquals("Cleaner", result[0].jobTitle)
    assertEquals("Tesco", result[0].employerName)

    verify {
      applicationRepository.findByPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    }
  }

  @Test
  fun `should return list of ExpressionOfInterestDTO with end date filter`() {
    val createdAt: Instant = Instant.parse("2023-03-06T00:00:00Z")

    val toDate = OffsetDateTime.parse("2023-05-01T00:00:00Z").toLocalDate()
    val testSarFilter = SARFilter(
      prn = prisonerNumber,
      toDate = toDate,
      fromDate = null,
    )

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
      expressionOfInterestRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    } returns expressions

    val result = service.fetchExpressionsOfInterest(testSarFilter).get()

    assertEquals(1, result.size)
    assertEquals("Car mechanic", result[0].jobTitle)
    assertEquals("The AA", result[0].employerName)
    verify {
      expressionOfInterestRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    }
  }

  @Test
  fun `should return empty list of ExpressionOfInterestDTO when created date is later than end date`() {
    val createdAt: Instant = Instant.parse("2023-03-06T00:00:00Z")

    val toDate = OffsetDateTime.parse("2022-04-01T00:00:00Z").toLocalDate()
    val testSarFilter = SARFilter(
      prn = prisonerNumber,
      toDate = toDate,
      fromDate = null,
    )

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
      expressionOfInterestRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    } returns emptyList()
    val result = service.fetchExpressionsOfInterest(testSarFilter).get()
    assertEquals(0, result.size)

    verify {
      expressionOfInterestRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
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
      expressionOfInterestRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    } returns expressions
    val result = service.fetchExpressionsOfInterest(sarFilter).get()
    assertEquals(1, result.size)
    assertEquals("Car mechanic", result[0].jobTitle)
    assertEquals("The AA", result[0].employerName)
    verify {
      expressionOfInterestRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }
  }

  @Test
  fun `should return empty list when no expressions of interest exist`() {
    every {
      expressionOfInterestRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    } returns emptyList()

    val result = service.fetchExpressionsOfInterest(sarFilter).get()

    assertTrue(result.isEmpty())
    verify {
      expressionOfInterestRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }
  }

  @Test
  fun `should return full list of archived jobs when end date filter is not used`() {
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
      archivedRepository.findByIdPrisonNumberOrderByCreatedAtDesc(prisonerNumber)
    } returns archivedJobs

    val result = service.fetchArchivedJobs(sarFilter).get()

    assertEquals(1, result.size)
    assertEquals("Sales person", result[0].jobTitle)
    assertEquals("Boots", result[0].employerName)
    verify {
      archivedRepository.findByIdPrisonNumberOrderByCreatedAtDesc(prisonerNumber)
    }
  }

  @Test
  fun `should return empty list archived jobs when created at is later than end date filter`() {
    val toDate = OffsetDateTime.parse("2024-04-01T00:00:00Z").toLocalDate()
    val createdAt = Instant.parse("2024-05-15T00:00:00Z")
    val testSarFilter = SARFilter(
      prn = prisonerNumber,
      toDate = toDate,
      fromDate = null,
    )

    every {
      archivedRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    } returns emptyList()

    val result = service.fetchArchivedJobs(testSarFilter).get()

    assertEquals(0, result.size)

    verify {
      archivedRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    }
  }

  @Test
  fun `should fetch archived jobs using end date filter`() {
    val toDate = OffsetDateTime.parse("2024-04-01T00:00:00Z").toLocalDate()
    val createdAt = Instant.parse("2024-03-15T00:00:00Z")
    val testSarFilter = SARFilter(
      prn = prisonerNumber,
      toDate = toDate,
      fromDate = null,
    )

    val archivedJob = Archived(
      job = mockk {
        every { title } returns "Warehouse Packer"
        every { employer.name } returns "Argos"
      },
      createdAt = createdAt,
      id = mockk(),
    )

    every {
      archivedRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    } returns listOf(archivedJob)

    val result = service.fetchArchivedJobs(testSarFilter).get()

    assertEquals(1, result.size)
    assertEquals("Warehouse Packer", result[0].jobTitle)
    assertEquals("Argos", result[0].employerName)

    verify {
      archivedRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(testSarFilter.prn, any())
    }
  }

  @Test
  fun `should return empty list when no archived jobs exist`() {
    every {
      archivedRepository.findByIdPrisonNumberOrderByCreatedAtDesc(eq(sarFilter.prn))
    } returns emptyList()

    val result = service.fetchArchivedJobs(sarFilter).get()

    assertTrue(result.isEmpty())
    verify {
      archivedRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }
  }

  private fun LocalDateTime.instant() = this.toInstant(ZoneOffset.UTC)
  private val LocalDate.endAt: Instant get() = this.atTime(atEndOfDay).instant()
}
