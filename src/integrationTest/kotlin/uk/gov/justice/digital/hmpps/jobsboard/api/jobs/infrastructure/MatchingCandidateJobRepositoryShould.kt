package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobsClosingSoonResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.MatchingCandidateJobRepository
import java.time.LocalDate

class MatchingCandidateJobRepositoryShould : JobRepositoryTestCase() {
  @Autowired
  private lateinit var matchingCandidateJobRepository: MatchingCandidateJobRepository

  @Autowired
  private lateinit var expressionOfInterestRepository: ExpressionOfInterestRepository

  @Autowired
  private lateinit var archivedRepository: ArchivedRepository

  private val prisonNumber = "A1234BC"
  private val anotherPrisonNumber = "X9876YZ"

  private val defaultPageable = PageRequest.of(0, 3)
  private val today = LocalDate.now()

  @Nested
  @DisplayName("Given no job has been created")
  inner class GivenNoJob {
    @Test
    fun `retrieve an empty job list`() {
      val results = matchingCandidateJobRepository.findJobsClosingSoon(prisonNumber, null, today, defaultPageable)
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
    fun `retrieve jobs closing soon`() {
      assertFindJobsClosingSoonIsExpected(
        expectedSize = allJobs.size,
        expectedJobs = allJobs,
      )
    }

    @Nested
    @DisplayName("And a job has been closed for application")
    inner class AndAJobClosed {
      private val today = amazonForkliftOperator.closingDate!!.plusDays(1)

      @Test
      fun `retrieve jobs closing soon, without closed job(s)`() {
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
        archiveJob(tescoWarehouseHandler, prisonNumber)
      }

      @Test
      fun `retrieve jobs closing soon, without archived job(s)`() {
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
        expressInterestToJob(tescoWarehouseHandler, prisonNumber)
      }

      @Test
      fun `retrieve jobs closing soon, without archived job(s)`() {
        val expectedJobs = listOf(amazonForkliftOperator, abcConstructionApprentice)
        assertFindJobsClosingSoonIsExpected(
          expectedSize = expectedJobs.size,
          expectedJobs = expectedJobs,
        )
      }
    }

    @Nested
    @DisplayName("And given another prisoner, with expressions of interest and archived job")
    inner class AndGivenAnotherPrisoner {
      @Test
      fun `retrieve jobs closing soon, with job archived for another prisoner`() {
        archiveJob(tescoWarehouseHandler, anotherPrisonNumber)
        assertFindAllJobsClosingSoonIsExpected()
      }

      @Test
      fun `retrieve jobs closing soon, with job of interest for another prisoner`() {
        expressInterestToJob(abcConstructionApprentice, anotherPrisonNumber)
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
      fun `retrieve jobs closing soon, of specified sectors only`() {
        val sectors = listOf(tescoWarehouseHandler.sector, amazonForkliftOperator.sector).map { it.lowercase() }
        val expectedJobs = listOf(amazonForkliftOperator, tescoWarehouseHandler)
        assertFindJobsClosingSoonIsExpected(
          sectors = sectors,
          expectedJobs = expectedJobs,
        )
      }

      @Test
      fun `retrieve jobs closing soon, of first or top 1 only`() {
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

    private fun archiveJob(job: Job, prisonNumber: String) = job.archivedBy(prisonNumber)
      .also { archivedRepository.saveAndFlush(it) }

    private fun expressInterestToJob(job: Job, prisonNumber: String) = job.registerExpressionOfInterest(prisonNumber)
      .also { expressionOfInterestRepository.saveAndFlush(it) }
  }

  private fun Job.closingSoonResponse() = GetJobsClosingSoonResponse.from(this)
}
