package uk.gov.justice.digital.hmpps.jobsboard.api.controller.sar

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uk.gov.justice.digital.hmpps.jobsboard.api.exceptions.NotFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.application.PrisonSubjectAccessRequestService
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.application.SubjectAccessRequestService
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ApplicationDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.HistoriesDTO
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import uk.gov.justice.hmpps.kotlin.sar.HmppsSubjectAccessRequestContent
import uk.gov.justice.hmpps.kotlin.sar.HmppsSubjectAccessRequestController
import java.time.LocalDate
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

class SubjectAccessRequestGetTest {
  private lateinit var subjectAccessRequestService: SubjectAccessRequestService
  private lateinit var prisonSubjectAccessRequestService: PrisonSubjectAccessRequestService
  private lateinit var controller: HmppsSubjectAccessRequestController
  private val objectMapper: ObjectMapper = jacksonObjectMapper()
  private val prisonNumber = "A1234BC"
  private val testCrn = "CRN123"

  @BeforeEach
  fun setUp() {
    subjectAccessRequestService = mockk()
    prisonSubjectAccessRequestService = PrisonSubjectAccessRequestService(subjectAccessRequestService)
    controller = HmppsSubjectAccessRequestController(prisonSubjectAccessRequestService)
  }

  @Test
  fun `should return SARSummaryDTO with empty histories in correct JSON format when PRN is valid`() {
    val prn = "A1234BC"
    val histories: List<HistoriesDTO> = emptyList()
    val createdAt = "2023-03-06T00:00:00Z"

    val applications = listOf(
      ApplicationDTO(
        "Delivery Driver",
        "Amazon Flex",
        "A1234BC",
        "Some",
        "One",
        "SELECTED_FOR_INTERVIEW",
        "MOI",
        histories,
        "2024-11-15T18:45:29.916505Z",
        "2024-11-25T09:45:29.916505Z",
      ),
    )
    val expressions = listOf(
      ExpressionOfInterestDTO(
        "Car mechanic",
        "The AA",
        "A1234BC",
        createdAt,
      ),
    )
    val archivedJobs = listOf(
      ArchivedDTO(
        "Sales person",
        "Boots",
        "A1234BC",
        createdAt,
      ),
    )

    every { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(applications)
    every { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(expressions)
    every { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(archivedJobs)

    val response: ResponseEntity<Any> = controller.getSarContentByReference(prn = prn, crn = null, fromDate = null, toDate = null)

    assertEquals(HttpStatus.OK, response.statusCode)

    val actualJson = objectMapper.writeValueAsString(response.body)

    val expectedJson = """
            {
              "content": {
                "applications": [
                  {
                    "jobTitle": "Delivery Driver",
                    "employerName": "Amazon Flex",
                    "prisonNumber": "A1234BC",
                    "prisonId": "MOI",
                    "firstName": "Some",
                    "lastName": "One",
                    "status": "SELECTED_FOR_INTERVIEW",
                    "histories": [],
                    "createdAt": "2024-11-15T18:45:29.916505Z",
                    "lastModifiedAt": "2024-11-25T09:45:29.916505Z"
                  }
                ],
                "expressionsOfInterest": [
                  {
                    "jobTitle": "Car mechanic",
                    "employerName": "The AA",
                    "prisonNumber": "A1234BC",
                    "createdAt": "2023-03-06T00:00:00Z"
                  }
                ],
                "archivedJobs": [
                  {
                    "jobTitle": "Sales person",
                    "employerName": "Boots",
                    "prisonNumber": "A1234BC",
                    "createdAt": "2023-03-06T00:00:00Z"
                  }
                ]
              }
            }
    """.trimIndent()

    val expectedObject = objectMapper.readValue<HmppsSubjectAccessRequestContent>(expectedJson)
    val actualObject = objectMapper.readValue<HmppsSubjectAccessRequestContent>(actualJson)

    assertEquals(expectedObject, actualObject)

    verify { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) }
  }

  @Test
  fun `should return SARSummaryDTO with populated histories in correct JSON format when PRN is valid`() {
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
        histories = histories, // Now populated
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

    val response: ResponseEntity<Any> = controller.getSarContentByReference(prn = prisonNumber, crn = null, fromDate = null, toDate = null)

    assertEquals(HttpStatus.OK, response.statusCode)

    val actualJson = objectMapper.writeValueAsString(response.body)

    val expectedJson = """
        {
          "content": {
            "applications": [
              {
                "jobTitle": "Delivery Driver",
                "employerName": "Amazon Flex",
                "prisonNumber": "A1234BC",
                "histories": [
                  {
                    "firstName": "Some",
                    "lastName": "One",
                    "status": "SELECTED_FOR_INTERVIEW",
                    "prisonId": "MOI",
                    "modifiedAt": "2024-11-25T09:45:29.916505Z"
                  },
                  {
                    "firstName": "Another",
                    "lastName": "Two",
                    "status": "OFFER_ACCEPTED",
                    "prisonId": "HML",
                    "modifiedAt": "2024-12-01T10:30:15.123456Z"
                  }
                ],
                "createdAt": "2024-11-15T18:45:29.916505Z",
                "lastModifiedAt": "2024-11-25T09:45:29.916505Z"
              }
            ],
            "expressionsOfInterest": [
              {
                "jobTitle": "Car mechanic",
                "employerName": "The AA",
                "prisonNumber": "A1234BC",
                "createdAt": "2023-03-06T00:00:00Z"
              }
            ],
            "archivedJobs": [
              {
                "jobTitle": "Sales person",
                "employerName": "Boots",
                "prisonNumber": "A1234BC",
                "createdAt": "2023-03-06T00:00:00Z"
              }
            ]
          }
        }
    """.trimIndent()

    val expectedObject = objectMapper.readValue<HmppsSubjectAccessRequestContent>(expectedJson)
    val actualObject = objectMapper.readValue<HmppsSubjectAccessRequestContent>(actualJson)

    assertEquals(expectedObject, actualObject)

    verify { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) }
    verify { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) }
  }

  @Test
  fun `should return 400 BAD REQUEST if both PRN and CRN are missing`() {
    val response: ResponseEntity<Any> = controller.getSarContentByReference(prn = null, crn = null, fromDate = null, toDate = null)
    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    val body = response.body as ErrorResponse
    assertEquals("One of prn or crn must be supplied.", body.userMessage)
  }

  @Test
  fun `should return 204 NO CONTENT if NotFoundException is thrown`() {
    every { subjectAccessRequestService.fetchApplications(match { it.prn == prisonNumber }) } throws NotFoundException("No content found")
    every { subjectAccessRequestService.fetchExpressionsOfInterest(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(emptyList())
    every { subjectAccessRequestService.fetchArchivedJobs(match { it.prn == prisonNumber }) } returns CompletableFuture.completedFuture(emptyList())

    val response: ResponseEntity<Any> = controller.getSarContentByReference(prn = prisonNumber, crn = null, fromDate = null, toDate = null)

    assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
  }

  @Test
  fun `should return 204 when all SAR lists are empty`() {
    every { subjectAccessRequestService.fetchApplications(any()) } returns CompletableFuture.completedFuture(emptyList<ApplicationDTO>())
    every { subjectAccessRequestService.fetchExpressionsOfInterest(any()) } returns CompletableFuture.completedFuture(emptyList<ExpressionOfInterestDTO>())
    every { subjectAccessRequestService.fetchArchivedJobs(any()) } returns CompletableFuture.completedFuture(emptyList<ArchivedDTO>())

    val response: ResponseEntity<Any> = controller.getSarContentByReference(prn = prisonNumber, crn = null, fromDate = null, toDate = null)
    assertEquals(response.statusCode.value(), 204)
    assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
  }

  @Test
  fun `should return 209 if only CRN is provided`() {
    val response: ResponseEntity<Any> = controller.getSarContentByReference(prn = null, crn = testCrn, fromDate = null, toDate = null)

    assertEquals(209, response.statusCode.value())
  }

  @Test
  fun `should return 400 if fromDate and toDate are provided without a prison identifier`() {
    val fromDate = LocalDate.of(2024, 2, 1)
    val toDate = LocalDate.of(2024, 1, 1)
    val response: ResponseEntity<Any> = controller.getSarContentByReference(prn = null, crn = null, fromDate = fromDate, toDate = toDate)

    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    val body = response.body as ErrorResponse
    assertEquals("One of prn or crn must be supplied.", body.userMessage)
  }
}
