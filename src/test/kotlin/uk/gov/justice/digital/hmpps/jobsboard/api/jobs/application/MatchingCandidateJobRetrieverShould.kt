package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
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
import java.util.stream.Stream

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
    assertThat(result.totalElements).isEqualTo(2)
    assertThat(result.totalPages).isEqualTo(1)

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

  @ParameterizedTest
  @MethodSource("exclusionTestProvider")
  fun `retrieveAllJobs should filter jobs based on various exclusion combinations`(
    candidateExclusions: List<String>,
    jobExclusions: String,
    shouldBeFiltered: Boolean,
  ) {
    // Given
    val job = createJobResponse(id = "1", exclusions = jobExclusions)

    whenever(
      matchingCandidateJobsRepository.findAll(
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
      ),
    ).thenReturn(PageImpl(listOf(job)))

    // When
    val result = matchingCandidateJobRetriever.retrieveAllJobs(
      prisonNumber = prisonNumber,
      sectors = null,
      releaseArea = null,
      searchRadius = null,
      pageable = pageable,
      offenceExclusions = candidateExclusions,
      employerId = null,
    )

    // Then
    if (shouldBeFiltered) {
      assertThat(result.content).isEmpty()
    } else {
      assertThat(result.content).hasSize(1)
      assertThat(result.content[0].id).isEqualTo("1")
    }
  }

  @Test
  fun `retrieveAllJobs should return original page if offenceExclusions is empty`() {
    // Given
    val job = createJobResponse(id = "1", exclusions = "ARSON")
    val dbPage = PageImpl(listOf(job))

    whenever(
      matchingCandidateJobsRepository.findAll(
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
      ),
    )
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
    verify(matchingCandidateJobsRepository).findAll(
      anyOrNull(),
      anyOrNull(),
      anyOrNull(),
      anyOrNull(),
      anyOrNull(),
      anyOrNull(),
      anyOrNull(),
      anyOrNull(),
    )
  }

  @ParameterizedTest
  @MethodSource("exclusionPagingMetaDataTestProvider")
  fun `retrieveAllJobs should return PageImpl with correctly filtered content and metadata`(
    candidateExclusions: List<String>,
    jobExclusionsList: List<String?>,
    expectedIdsRemaining: List<String>,
  ) {
    // Given
    val pageRequest = PageRequest.of(0, 10)
    val jobsFromDb = jobExclusionsList.mapIndexed { index, exclusions ->
      createJobResponse(id = (index + 1).toString(), exclusions = exclusions ?: "")
    }

    // The repository returns the "raw" unfiltered list
    val dbPage = PageImpl(jobsFromDb, pageRequest, jobsFromDb.size.toLong())

    whenever(
      matchingCandidateJobsRepository.findAll(
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
        anyOrNull(),
      ),
    ).thenReturn(dbPage)

    // When
    val result = matchingCandidateJobRetriever.retrieveAllJobs(
      prisonNumber = prisonNumber,
      sectors = null,
      releaseArea = null,
      searchRadius = null,
      pageable = pageRequest,
      isNationalJob = false,
      employerId = null,
      offenceExclusions = candidateExclusions,
    )

    // Then
    // 1. Verify the content contains only the expected IDs
    assertThat(result.content.map { it.id }).containsExactlyElementsOf(expectedIdsRemaining)

    // 2. Verify the Pageable metadata is preserved
    assertThat(result.pageable).isEqualTo(pageRequest)

    // 3. Verify totalElements matches the size of the filtered list (per your implementation)
    assertThat(result.totalElements).isEqualTo(expectedIdsRemaining.size.toLong())
  }

  companion object {

    @JvmStatic
    fun exclusionPagingMetaDataTestProvider(): Stream<Arguments> = Stream.of(
      // (Candidate Exclusions, Job Exclusions in DB, Expected IDs to remain)

      // Scenario: Multiple candidate exclusions vs multiple job exclusions
      Arguments.of(
        listOf("ARSON", "THEFT"),
        listOf("MURDER, ARSON", "DRIVING", "THEFT, FRAUD"),
        listOf("2"),
      ),
      // Scenario: One match, one safe
      Arguments.of(
        listOf("ARSON"),
        listOf("ARSON", "NONE"),
        listOf("2"),
      ),
      // Scenario: Case insensitivity and trimming
      Arguments.of(
        listOf(" arson "),
        listOf("ARSON", " murder ", "CLEAN"),
        listOf("2", "3"),
      ),
      // Scenario: Empty candidate exclusions (should return everything)
      Arguments.of(
        emptyList<String>(),
        listOf("ARSON", "MURDER"),
        listOf("1", "2"),
      ),
    )

    @JvmStatic
    fun exclusionTestProvider(): Stream<Arguments> = Stream.of(
      // candidateExclusions, jobExclusions, shouldBeFiltered
      Arguments.of(listOf("arson"), "ARSON", true),
      Arguments.of(listOf("arson"), " ARSON", true),
      Arguments.of(listOf("arson"), "ARSON ", true),
      Arguments.of(listOf(" arson "), "  ARSON  ", true),
      Arguments.of(listOf("arson "), "  ARSON  ", true),
      Arguments.of(listOf(" arson"), "  ARSON  ", true),
      Arguments.of(listOf("ARSON"), "MURDER, ARSON, THEFT", true),
      Arguments.of(listOf("MURDER"), "ARSON", false),
      Arguments.of(listOf("DRIVING"), "DRIVING_OFFENCE", false),
      Arguments.of(listOf("SEXUAL"), "NONE", false),
      Arguments.of(listOf("arson", "murder"), "MURDER", true),
      Arguments.of(listOf("ARSON"), "arson,murder", true),
    )
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
