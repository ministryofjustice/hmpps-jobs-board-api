package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.exceptions.NotFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ApplicationDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.HistoriesDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARContentDTO
import uk.gov.justice.hmpps.kotlin.sar.HmppsSubjectAccessRequestContent
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

class PrisonSubjectAccessRequestServiceTest {
  private lateinit var subjectAccessRequestService: SubjectAccessRequestService
  private lateinit var prisonSubjectAccessRequestService: PrisonSubjectAccessRequestService
  private val prisonNumber = "A1234BC"

  @BeforeEach
  fun setUp() {
    subjectAccessRequestService = mockk()
    prisonSubjectAccessRequestService = PrisonSubjectAccessRequestService(subjectAccessRequestService)
  }

  @Test
  fun `should return HmppsSubjectAccessRequestContent when all SAR lists are populated`() {
    val prn = "A1234BC"
    val createdAt = "2023-03-06T00:00:00Z"

    val histories = listOf(
      HistoriesDTO(
        firstName = "Some",
        lastName = "One",
        status = "SELECTED_FOR_INTERVIEW",
        prisonId = "MOI",
        modifiedAt = "2024-11-25T09:45:29.916505Z",
      ),
      HistoriesDTO(
        firstName = "Another",
        lastName = "Two",
        status = "OFFER_ACCEPTED",
        prisonId = "HML",
        modifiedAt = "2024-12-01T10:30:15.123456Z",
      ),
    )

    val applications = listOf(
      ApplicationDTO(
        jobTitle = "Delivery Driver",
        employerName = "Amazon Flex",
        prisonNumber = prn,
        null,
        null,
        null,
        null,
        histories = histories,
        createdAt = "2024-11-15T18:45:29.916505Z",
        lastModifiedAt = "2024-11-25T09:45:29.916505Z",
      ),
    )

    val expressions = listOf(
      ExpressionOfInterestDTO(
        jobTitle = "Car mechanic",
        employerName = "The AA",
        prisonNumber = prn,
        createdAt = createdAt,
      ),
    )

    val archivedJobs = listOf(
      ArchivedDTO(
        jobTitle = "Sales person",
        employerName = "Boots",
        prisonNumber = prn,
        createdAt = createdAt,
      ),
    )

    every { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(applications)
    every { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(expressions)
    every { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(archivedJobs)

    val response = prisonSubjectAccessRequestService.getPrisonContentFor(prn = prisonNumber, fromDate = null, toDate = null)

    val expected = HmppsSubjectAccessRequestContent(content=SARContentDTO(applications = applications, expressionsOfInterest = expressions, archivedJobs = archivedJobs))

    assertEquals(expected, response)

    verify { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) }
  }

  @Test
  fun `should return HmppsSubjectAccessRequestContent when one SAR lists is populated`() {
    val prn = "A1234BC"
    val createdAt = "2023-03-06T00:00:00Z"

    val applications = listOf(
      ApplicationDTO(
        jobTitle = "Delivery Driver",
        employerName = "Amazon Flex",
        prisonNumber = prn,
        null,
        null,
        null,
        null,
        histories = emptyList(),
        createdAt = "2024-11-15T18:45:29.916505Z",
        lastModifiedAt = "2024-11-25T09:45:29.916505Z",
      ),
    )

    every { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(applications)
    every { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(emptyList<ExpressionOfInterestDTO>())
    every { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(emptyList<ArchivedDTO>())

    val response = prisonSubjectAccessRequestService.getPrisonContentFor(prn = prisonNumber, fromDate = null, toDate = null)

    val expected = HmppsSubjectAccessRequestContent(content=SARContentDTO(applications = applications, expressionsOfInterest = emptyList(), archivedJobs = emptyList()))

    assertEquals(expected, response)

    verify { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) }
  }

  @Test
  fun `should return null when all SAR lists are empty`() {
    every { subjectAccessRequestService.fetchApplications(any()) } returns CompletableFuture.completedFuture(emptyList<ApplicationDTO>())
    every { subjectAccessRequestService.fetchExpressionsOfInterest(any()) } returns CompletableFuture.completedFuture(emptyList<ExpressionOfInterestDTO>())
    every { subjectAccessRequestService.fetchArchivedJobs(any()) } returns CompletableFuture.completedFuture(emptyList<ArchivedDTO>())

    val response = prisonSubjectAccessRequestService.getPrisonContentFor(prn = prisonNumber, fromDate = null, toDate = null)
    assertEquals(null, response)
  }

  @Test
  fun `should return null when NotFoundException is thrown`() {
    every { subjectAccessRequestService.fetchApplications(any()) } throws NotFoundException(prisonNumber)
    every { subjectAccessRequestService.fetchExpressionsOfInterest(any()) } returns CompletableFuture.completedFuture(emptyList<ExpressionOfInterestDTO>())
    every { subjectAccessRequestService.fetchArchivedJobs(any()) } returns CompletableFuture.completedFuture(emptyList<ArchivedDTO>())

    val response = prisonSubjectAccessRequestService.getPrisonContentFor(prn = prisonNumber, fromDate = null, toDate = null)
    assertEquals(null, response)
  }
}
