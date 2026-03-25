package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import java.time.Instant
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class MatchingCandidateJobRetrieverShould : TestBase() {

  @Mock
  private lateinit var matchingCandidateJobsRepository: MatchingCandidateJobRepository

  @InjectMocks
  private lateinit var matchingCandidateJobRetriever: MatchingCandidateJobRetriever

  private val today = LocalDate.now()
  private val prisonNumber = "A1234BC"
  private val pageable = PageRequest.of(0, 10)

  @BeforeEach
  fun setUp() {
    whenever(timeProvider.today()).thenReturn(today)
  }

  @Test
  fun `retrieveAllJobs should filter out jobs matching candidate offence exclusions`() {
    // Given
    val offenceExclusions = listOf("ARSON", "DRIVING")

    val job1 = createJobResponse(id = "1", exclusions = "NONE")
    val job2 = createJobResponse(id = "2", exclusions = "ARSON, OTHER") // Contains ARSON
    val job3 = createJobResponse(id = "3", exclusions = "DRIVING") // Contains DRIVING
    val job4 = createJobResponse(id = "4", exclusions = "CASE_BY_CASE")

    val dbPage = PageImpl(listOf(job1, job2, job3, job4), pageable, 4)

    whenever(
      matchingCandidateJobsRepository.findAll(
        prisonNumber = eq(prisonNumber),
        sectors = anyOrNull(),
        releaseArea = anyOrNull(),
        searchRadius = anyOrNull(),
        currentDate = eq(today),
        isNationalJob = anyOrNull(),
        employerId = anyOrNull(),
        pageable = eq(pageable),
      ),
    ).thenReturn(dbPage)

    // When
    val result = matchingCandidateJobRetriever.retrieveAllJobs(
      prisonNumber = prisonNumber,
      sectors = listOf("CONSTRUCTION"),
      releaseArea = "S1 1AA",
      searchRadius = 5,
      pageable = pageable,
      isNationalJob = false,
      employerId = "emp-123",
      offenceExclusions = offenceExclusions,
    )

    // Then
    // Only Job 1 and Job 4 remain
    assertThat(result.content).hasSize(2)
    assertThat(result.content.map { it.id }).containsExactly("1", "4")

    verify(matchingCandidateJobsRepository, times(1)).findAll(
      eq(prisonNumber),
      anyOrNull(),
      anyOrNull(),
      anyOrNull(),
      eq(today),
      anyOrNull(),
      anyOrNull(),
      eq(pageable),
    )
  }

  @Test
  fun `retrieveAllJobs should be case insensitive and handle whitespace when filtering`() {
    // Given
    val offenceExclusions = listOf(" arson ") // Lowercase with space
    val job = createJobResponse(id = "1", exclusions = "  ARSON  ") // Uppercase with space

    whenever(matchingCandidateJobsRepository.findAll(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
      .thenReturn(PageImpl(listOf(job)))

    // When
    val result = matchingCandidateJobRetriever.retrieveAllJobs(
      prisonNumber = prisonNumber,
      sectors = null,
      releaseArea = null,
      searchRadius = null,
      pageable = pageable,
      offenceExclusions = offenceExclusions,
      employerId = null,
    )

    // Then
    assertThat(result.content).isEmpty()
  }

  @Test
  fun `retrieveAllJobs should return original page if offenceExclusions is empty`() {
    // Given
    val job = createJobResponse(id = "1", exclusions = "ARSON")
    val dbPage = PageImpl(listOf(job))

    whenever(matchingCandidateJobsRepository.findAll(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
      .thenReturn(dbPage)

    // When
    val result = matchingCandidateJobRetriever.retrieveAllJobs(
      prisonNumber = prisonNumber,
      sectors = null,
      releaseArea = null,
      searchRadius = null,
      pageable = pageable,
      offenceExclusions = emptyList(),
      employerId = null,
    )

    // Then
    assertThat(result.content).hasSize(1)
    verify(matchingCandidateJobsRepository).findAll(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
  }

  private fun createJobResponse(id: String, exclusions: String) = GetMatchingCandidateJobsResponse(
    id = id,
    jobTitle = "Job $id",
    employerName = "Employer",
    sector = "Sector",
    postcode = "S1 1AA",
    closingDate = today.plusDays(7),
    offenceExclusions = exclusions,
    hasExpressedInterest = false,
    createdAt = Instant.now(),
    distance = 1.0f,
    isNational = false,
    numberOfVacancies = 1,
  )
}
