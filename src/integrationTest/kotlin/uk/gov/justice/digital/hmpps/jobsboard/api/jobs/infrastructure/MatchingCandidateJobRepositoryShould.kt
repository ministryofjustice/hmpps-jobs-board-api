package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.JpaSort
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobsClosingSoonResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.CALC_DISTANCE_EXPRESSION
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodeMother
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository
import java.time.LocalDate

class MatchingCandidateJobRepositoryShould : JobRepositoryTestCase() {
  @Autowired
  private lateinit var matchingCandidateJobRepository: MatchingCandidateJobRepository

  @Autowired
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @Autowired
  private lateinit var archivedRepository: ArchivedRepository

  @Autowired
  private lateinit var postcodesRepository: PostcodesRepository

  private val prisonNumber = "A1234BC"
  private val anotherPrisonNumber = "X9876YZ"

  private val defaultPageable = PageRequest.of(0, 3)
  private val paginatedSortByClosingDateAsc = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "closingDate"))
  private val today = LocalDate.now()

  @Nested
  @DisplayName("Given no job has been created")
  inner class GivenNoJob {
    @Test
    fun `retrieve an empty list of matched jobs closing soon`() {
      val results = matchingCandidateJobRepository.findJobsClosingSoon(prisonNumber, null, today, defaultPageable)
      assertThat(results).isEmpty()
    }

    @Test
    fun `retrieve an empty list of jobs of interest closing soon`() {
      val results = matchingCandidateJobRepository.findJobsOfInterestClosingSoon(prisonNumber, today)
      assertThat(results).isEmpty()
    }

    @Test
    fun `retrieve an empty list of jobs of interest`() {
      val results = matchingCandidateJobRepository.findJobsOfInterest(prisonNumber, null, today, defaultPageable)
      assertThat(results).isEmpty()
    }
  }

  @Nested
  @DisplayName("Given some jobs have been created")
  inner class GivenSomeJobsCreated {
    private val allJobs = listOf(amazonForkliftOperator, tescoWarehouseHandler, abcConstructionApprentice)

    @BeforeEach
    fun setUp() {
      allJobs.toTypedArray().let { givenJobsHaveBeenCreated(*it) }
    }

    @Test
    fun `retrieve matched jobs closing soon`() {
      assertFindJobsClosingSoonIsExpected(
        expectedSize = allJobs.size,
        expectedJobs = allJobs,
      )
    }

    @Test
    fun `retrieve empty job list of interest closing soon`() {
      assertFindJobsOfInterestClosingSoonIsExpected(expectedSize = 0)
    }

    @Test
    fun `retrieve empty job list of interest`() {
      assertFindJobsOfInterestIsExpected(expectedSize = 0)
    }

    @Nested
    @DisplayName("And a job has been closed for application")
    inner class AndAJobClosed {
      private val today = amazonForkliftOperator.closingDate!!.plusDays(1)

      @Test
      fun `retrieve matched jobs closing soon, without closed job(s)`() {
        val expectedJobs = listOf(tescoWarehouseHandler, abcConstructionApprentice)
        assertFindJobsClosingSoonIsExpected(
          currentDate = today,
          expectedSize = expectedJobs.size,
          expectedJobs = expectedJobs,
        )
      }
    }

    @Nested
    @DisplayName("And a job has been archived, for the given prisoner")
    inner class AndAJobArchived {
      @BeforeEach
      fun setup() {
        archiveJob(prisonNumber, tescoWarehouseHandler)
      }

      @Test
      fun `retrieve matched jobs closing soon, without archived job(s)`() {
        val expectedJobs = listOf(amazonForkliftOperator, abcConstructionApprentice)
        assertFindJobsClosingSoonIsExpected(
          expectedSize = expectedJobs.size,
          expectedJobs = expectedJobs,
        )
      }
    }

    @Nested
    @DisplayName("And a job of interest, for the given prisoner")
    inner class AndExpressionOfInterest {
      @BeforeEach
      fun setup() {
        expressInterestToJob(prisonNumber, tescoWarehouseHandler)
      }

      @Test
      fun `retrieve matched jobs closing soon, without job(s) of interest`() {
        val expectedJobs = listOf(amazonForkliftOperator, abcConstructionApprentice)
        assertFindJobsClosingSoonIsExpected(
          expectedSize = expectedJobs.size,
          expectedJobs = expectedJobs,
        )
      }
    }

    @Nested
    @DisplayName("And some jobs of interest, for the given prisoner")
    inner class AndSomeExpressionsOfInterest {
      @BeforeEach
      fun setUp() {
        expressInterestToJob(prisonNumber, *allJobs.toTypedArray())
      }

      @Test
      fun `retrieve jobs of interest closing soon`() {
        val expectedJobs = allJobs
        assertFindJobsOfInterestClosingSoonIsExpected(
          expectedSize = expectedJobs.size,
          expectedJobs = expectedJobs,
        )
      }

      @Nested
      @DisplayName("And a job of interest has been closed")
      inner class AndAJobClosed {
        private lateinit var today: LocalDate
        private lateinit var expectedJobs: List<Job>

        @BeforeEach
        fun setUp() {
          expectedJobs = listOf(tescoWarehouseHandler, abcConstructionApprentice)
          today = amazonForkliftOperator.closingDate!!.plusDays(1)
        }

        @Test
        fun `retrieve jobs of interest closing soon, without closed job(s)`() {
          assertFindJobsOfInterestClosingSoonIsExpected(
            currentDate = today,
            expectedSize = expectedJobs.size,
            expectedJobs = expectedJobs,
          )
        }

        @Test
        fun `retrieve jobs of interest, without closed job(s)`() {
          val expectedResults = expectedJobs.map { it.listResponse(true) }
          assertFindJobsOfInterestIsExpected(
            currentDate = today,
            expectedSize = expectedJobs.size,
            expectedResults = expectedResults,
          )
        }
      }

      @Nested
      @DisplayName("And a job of interest has been archived, for the given prisoner")
      inner class AndAJobArchived {
        private lateinit var expectedJobs: List<Job>

        @BeforeEach
        fun setUp() {
          expectedJobs = listOf(amazonForkliftOperator, abcConstructionApprentice)
          archiveJob(prisonNumber, tescoWarehouseHandler)
        }

        @Test
        fun `retrieve jobs of interest closing soon, without archived job(s)`() {
          assertFindJobsOfInterestClosingSoonIsExpected(
            expectedSize = expectedJobs.size,
            expectedJobs = expectedJobs,
          )
        }

        @Test
        fun `retrieve jobs of interest, without archived job(s)`() {
          val expectedResults = expectedJobs.map { it.listResponse(true) }
          assertFindJobsOfInterestIsExpected(
            expectedSize = expectedJobs.size,
            expectedResults = expectedResults,
          )
        }
      }

      @Nested
      @DisplayName("And release area has been specified, for the given prisoner")
      inner class AndReleaseArea {
        private lateinit var expectedJobs: List<Job>
        private lateinit var expectedResults: List<GetMatchingCandidateJobsResponse>
        private val releaseArea = "LS110AN"

        @BeforeEach
        fun setUp() {
          postcodesRepository.deleteAll()
          givenSomePostcodes()

          expectedJobs = allJobs
          expectedResults = listOf(
            amazonForkliftOperator.listResponse(true, 0.6f),
            tescoWarehouseHandler.listResponse(true, 83.3f),
            abcConstructionApprentice.listResponse(true, 83.3f),
          )
        }

        @Test
        fun `retrieve jobs of interest with distance`() {
          assertFindJobsOfInterestIsExpected(
            currentDate = today,
            releaseAreaPostcode = releaseArea,
            expectedSize = expectedJobs.size,
            expectedResults = expectedResults,
          )
        }

        @Test
        fun `retrieve jobs of interest with distance, sorted by distance`() {
          val sort = CALC_DISTANCE_EXPRESSION.let {
            JpaSort.unsafe(Sort.Direction.ASC, it)
          }
          val pageableSortByDistance = PageRequest.of(0, 20, sort)
          assertFindJobsOfInterestIsExpected(
            currentDate = today,
            releaseAreaPostcode = releaseArea,
            pageable = pageableSortByDistance,
            expectedSize = expectedJobs.size,
            expectedResults = expectedResults,
          )
        }
      }
    }

    @Nested
    @DisplayName("And given another prisoner, with expressions of interest and archived job")
    inner class AndGivenAnotherPrisoner {
      @Test
      fun `retrieve matched jobs closing soon, with job archived for another prisoner`() {
        archiveJob(anotherPrisonNumber, tescoWarehouseHandler)
        assertFindAllJobsClosingSoonIsExpected()
      }

      @Test
      fun `retrieve matched jobs closing soon, with job of interest for another prisoner`() {
        expressInterestToJob(anotherPrisonNumber, abcConstructionApprentice)
        assertFindAllJobsClosingSoonIsExpected()
      }

      private fun assertFindAllJobsClosingSoonIsExpected() {
        val expectedJobs = allJobs
        assertFindJobsClosingSoonIsExpected(
          expectedSize = expectedJobs.size,
          expectedJobs = expectedJobs,
        )
      }
    }

    @Nested
    @DisplayName("And custom filter or parameter has been specified")
    inner class AndCustomisation {
      @Test
      fun `retrieve matched jobs closing soon, of specified sectors only`() {
        val sectors = listOf(tescoWarehouseHandler.sector, amazonForkliftOperator.sector).map { it.lowercase() }
        val expectedJobs = listOf(amazonForkliftOperator, tescoWarehouseHandler)
        assertFindJobsClosingSoonIsExpected(
          sectors = sectors,
          expectedJobs = expectedJobs,
        )
      }

      @Test
      fun `retrieve matched jobs closing soon, of first or top 1 only`() {
        val expectedJobs = listOf(amazonForkliftOperator)
        assertFindJobsClosingSoonIsExpected(
          pageable = PageRequest.of(0, 1),
          expectedSize = expectedJobs.size,
          expectedJobs = expectedJobs,
        )
      }
    }

    private fun assertFindJobsClosingSoonIsExpected(
      givenPrisonNumber: String = prisonNumber,
      sectors: List<String>? = null,
      currentDate: LocalDate = today,
      pageable: Pageable = defaultPageable,
      expectedSize: Int? = null,
      expectedJobs: List<Job>? = null,
    ) {
      val results = matchingCandidateJobRepository.findJobsClosingSoon(givenPrisonNumber, sectors, currentDate, pageable)

      expectedSize?.let {
        assertThat(results).hasSize(expectedSize)
      }
      expectedJobs?.let {
        val expectedResults = expectedJobs.map { it.closingSoonResponse() }
        assertThat(results).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(expectedResults)
      }
    }

    private fun assertFindJobsOfInterestClosingSoonIsExpected(
      givenPrisonNumber: String = prisonNumber,
      currentDate: LocalDate = today,
      expectedSize: Int? = null,
      expectedJobs: List<Job>? = null,
    ) {
      val results = matchingCandidateJobRepository.findJobsOfInterestClosingSoon(givenPrisonNumber, currentDate)
      expectedSize?.let {
        assertThat(results).hasSize(expectedSize)
      }
      expectedJobs?.let {
        val expectedResults = expectedJobs.map { it.closingSoonResponse() }
        assertThat(results).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(expectedResults)
      }
    }

    private fun archiveJob(prisonNumber: String, job: Job) = job.archivedBy(prisonNumber)
      .also { archivedRepository.saveAndFlush(it) }

    private fun expressInterestToJob(prisonNumber: String, vararg jobs: Job) = jobs.forEach { job ->
      job.registerExpressionOfInterest(prisonNumber)
        .also { expressionOfInterestRepository.saveAndFlush(it) }
    }

    private fun assertFindJobsOfInterestIsExpected(
      givenPrisonNumber: String = prisonNumber,
      releaseAreaPostcode: String? = null,
      currentDate: LocalDate = today,
      pageable: Pageable = paginatedSortByClosingDateAsc,
      expectedSize: Int? = null,
      expectedResults: List<GetMatchingCandidateJobsResponse>? = null,
    ) {
      val results =
        matchingCandidateJobRepository.findJobsOfInterest(givenPrisonNumber, releaseAreaPostcode, currentDate, pageable)
      expectedSize?.let {
        assertThat(results).hasSize(expectedSize)
      }
      expectedResults?.let {
        assertThat(results.content).usingRecursiveComparison().ignoringFields("createdAt").isEqualTo(expectedResults)
      }
    }

    private fun givenSomePostcodes() {
      PostcodeMother.postcodeMap.values.let {
        postcodesRepository.saveAllAndFlush(it)
      }
    }
  }

  private fun Job.closingSoonResponse() = GetJobsClosingSoonResponse.from(this)
  private fun Job.listResponse(hasExpressedInterest: Boolean = false, distance: Float? = null) = GetMatchingCandidateJobsResponse(
    id = id.id,
    jobTitle = title,
    employerName = employer.name,
    sector = sector,
    postcode = postcode,
    closingDate = closingDate,
    hasExpressedInterest = hasExpressedInterest,
    createdAt = createdAt,
    distance = distance,
  )
}
