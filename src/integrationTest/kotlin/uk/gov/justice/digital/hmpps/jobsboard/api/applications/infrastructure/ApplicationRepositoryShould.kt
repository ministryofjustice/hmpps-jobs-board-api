package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonABC
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonMDI
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother.prisonXYZ
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.abcConstruction
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.tesco
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.util.*

class ApplicationRepositoryShould : ApplicationRepositoryTestCase() {
  private val firstAuditor = ApplicationMother.createdBy
  private val subsequentAuditor = ApplicationMother.lastModifiedBy

  private var currentAuditor = firstAuditor

  @BeforeEach
  override fun setUp() {
    applicationRepository.deleteAll()
    super.setUp()
    setCurrentAuditor(currentAuditor)
  }

  @Test
  fun `return false, when checking existence of non-existent application`() {
    val applicationIdNotExist = EntityId(UUID.randomUUID().toString())
    val exist = applicationRepository.existsById(applicationIdNotExist)
    assertThat(exist).isFalse()
  }

  @Nested
  @DisplayName("Given the known application")
  inner class GivenTheKnownApplicant {
    private val knownApplicant = ApplicationMother.knownApplicant

    @Test
    fun `submit an application by user on behalf of the given prisoner`() {
      val application = ApplicationMother.builder().apply { job = givenAJobHasBeenCreated() }.build()
      val savedApplication = applicationRepository.saveAndFlush(application)

      assertEquals(savedApplication, application)
    }

    @Nested
    @DisplayName("And an application has been made")
    inner class AndAnApplicationMade {
      private lateinit var application: Application

      @BeforeEach
      fun beforeEach() {
        this.application = givenAnApplicationMade()
      }

      @Test
      fun `return true, when checking existence of existing application`() {
        val exist = applicationRepository.existsById(application.id)
        assertThat(exist).isTrue()
      }

      @Test
      fun `retrieve the application by application ID`() {
        val actualApplication = applicationRepository.findById(application.id)
        assertThat(actualApplication).isNotNull
        assertEquals(actualApplication.get(), application)
      }

// //      @Test
// //      fun `retrieve the latest revision of the application`() {
// //        val revisions = applicationRepository.findRevisions(application.id)
// //
// //        assertThat(revisions).isNotEmpty
// //        revisions.latestRevision.let { latest ->
// //          with(latest.metadata) {
// //            assertThat(revisionType).isEqualTo(RevisionType.INSERT)
// //            assertThat(getDelegate<RevisionInfo>().createdBy).isEqualTo(currentAuditor)
// //          }
// //          assertEquals(latest.entity, this.application)
// //          with(latest.entity) {
// //            assertThat(createdBy).isNotNull
// //            assertThat(lastModifiedBy).isNotNull
// //            assertThat(createdAt).isNotNull
// //            assertThat(lastModifiedAt).isNotNull
// //          }
// //        }
// //      }
//
// //      @Test
// //      fun `retrieve all revisions of the application, when it has been updated multiple times`() {
// //        setCurrentAuditor(subsequentAuditor)
// //        val updateCount = 3
// //        repeat(updateCount) { index ->
// //          ApplicationBuilder().from(application).apply {
// //            additionalInformation = "updating info: ${index + 1}"
// //          }.build().let {
// //            application = applicationRepository.saveAndFlush(it)
// //          }
// //        }
// //        val expectedRevisionCount = updateCount + 1
// //
// //        val revisions = applicationRepository.findRevisions(application.id)
// //        assertThat(revisions).isNotEmpty
// //        assertThat(revisions.content.count()).isEqualTo(expectedRevisionCount)
// //        assertRevisionMetadata(RevisionType.UPDATE, currentAuditor, revisions.latestRevision)
// //        assertRevisionMetadata(RevisionType.INSERT, firstAuditor, revisions.content[0])
// //        assertRevisionMetadata(
// //          expectedRevisionType = RevisionType.UPDATE,
// //          expectedCreator = subsequentAuditor,
// //          revisions = revisions.content.subList(1, revisions.content.size).toTypedArray(),
// //        )
//    }
    }

    @Nested
    @DisplayName("And three applications have been made")
    inner class AndThreeApplicationsMade {
      private lateinit var applications: List<Application>

      @BeforeEach
      fun beforeEach() {
        applications = givenThreeApplicationsMade()
      }

      @Test
      fun `retrieve only open applications for given prisoner`() {
        assertRetrieveApplicationsOfGivenStatusOnly(
          prisonNumber = knownApplicant.prisonNumber,
          status = ApplicationStatus.openStatus.map { it.name },
          expectedContentSize = 2,
        )
      }

      @Test
      fun `retrieve only closed applications for given prisoner`() {
        assertRetrieveApplicationsOfGivenStatusOnly(
          prisonNumber = knownApplicant.prisonNumber,
          status = ApplicationStatus.closedStatus.map { it.name },
          expectedContentSize = 1,
        )
      }

      private fun assertRetrieveApplicationsOfGivenStatusOnly(
        prisonNumber: String,
        status: List<String>,
        expectedContentSize: Int,
      ) {
        val pageable: Pageable = PageRequest.of(0, 10, Sort.by(ASC, "createdAt"))
        val applications = applicationRepository.findByPrisonNumberAndStatusIn(prisonNumber, status, pageable)

        assertThat(applications).isNotNull
        assertThat(applications.content.size).isEqualTo(expectedContentSize)
        applications.content.forEach {
          assertThat(it.prisonNumber).isEqualTo(prisonNumber)
          assertThat(it.status).isIn(status)
        }
      }
    }

    @Nested
    @DisplayName("And a long username")
    inner class AndALongUsername {
      private val longUsername = "${"A".repeat(234)}@a.com"

      @BeforeEach
      fun setUp() {
        setCurrentAuditor(longUsername)
      }

      @Test
      fun `create new employer with a long username`() {
        val application = ApplicationMother.builder().apply { job = givenAJobHasBeenCreated() }.build()
        val savedApplication = applicationRepository.saveAndFlush(application)

        with(savedApplication) {
          assertThat(createdBy).hasSize(240)
          assertThat(lastModifiedBy).hasSize(240)
        }
      }
    }
  }

  @Nested
  @DisplayName("Given some applications made for prisoners from multiple prisons")
  inner class GivenApplicationsMadeFromMultiplePrisons {
    private val defaultPageable: Pageable = PageRequest.of(0, 20, Sort.by(ASC, "lastName", "firstName"))

    @BeforeEach
    fun setUp() = givenMoreApplicationsFromMultiplePrisons()

    @Test
    fun `retrieve all applications of given prison, when only prison ID is specified`() {
      val prisonId = prisonMDI
      val applicationPage = applicationRepository.findByPrisonId(prisonId, defaultPageable)
      assertThat(applicationPage).isNotEmpty.hasSize(3)
      applicationPage.forEach { assertThat(it.prisonId).isEqualTo(prisonId) }
    }

    @Test
    fun `retrieve no application of another prison, when only prison ID is specified and no application there`() {
      val applicationPage = applicationRepository.findByPrisonId(prisonXYZ, defaultPageable)
      assertThat(applicationPage).isEmpty()
    }

    @Nested
    @DisplayName("And optional parameter(s) has/have been specified")
    inner class AndGivenPrisonIdAndOptionalParameters {
      private val prisonId = prisonABC

      @Test
      fun `retrieve relevant applications, when searching with applicationStatus`() {
        assertFindByStatusIsAsExpected(listOf(ApplicationStatus.APPLICATION_MADE.toString()), 4)
        listOf(
          ApplicationStatus.APPLICATION_UNSUCCESSFUL,
          ApplicationStatus.SELECTED_FOR_INTERVIEW,
          ApplicationStatus.INTERVIEW_BOOKED,
          ApplicationStatus.UNSUCCESSFUL_AT_INTERVIEW,
          ApplicationStatus.JOB_OFFER,
        ).map { it.name }.forEach {
          assertFindByStatusIsAsExpected(listOf(it), 1)
        }

        // APPLICATION_MADE: 4, SELECTED_FOR_INTERVIEW: 1, INTERVIEW_BOOKED: 1
        assertFindByStatusIsAsExpected(ApplicationStatus.openStatus.map { it.name }, 4 + 1 + 1)

        // APPLICATION_UNSUCCESSFUL, UNSUCCESSFUL_AT_INTERVIEW, JOB_OFFER
        assertFindByStatusIsAsExpected(ApplicationStatus.closedStatus.map { it.name }, 1 * 3)
      }

      @Test
      fun `retrieve relevant applications, when searching with prisonerName`() {
        // Applicant  Name (Last, First)  First Last    Number of Application
        //    A       One                 One           3
        //    B       One, Double         Double One    3
        //    C       Half, Three         Three Half    1
        //    D       Three Half,         Three Half    1
        //    E       , Half Three        Half Three    1

        // applicant B : 3
        assertFindByPrisonerNameIsAsExpected("Double", 3, setOf("Double One"))

        // applicant A, B : 3+3
        assertFindByPrisonerNameIsAsExpected("One", 6, setOf("One", "Double One"))

        // applicant C, D, E: 1x3
        assertFindByPrisonerNameIsAsExpected("Three", 3, setOf("Three Half", "Half Three"))
        // applicant C, D, E: 1x3
        assertFindByPrisonerNameIsAsExpected("Half", 3, setOf("Three Half", "Half Three"))

        // applicant C, D: 1x2
        assertFindByPrisonerNameIsAsExpected("Three Half", 2, setOf("Three Half"))
        // application E: 1
        assertFindByPrisonerNameIsAsExpected("Half Three", 1, setOf("Half Three"))
      }

      @Test
      fun `retrieve relevant applications, when searching with prisonerName of mixed cases`() {
        assertFindByPrisonerNameIsAsExpected("DOUBLE", 3)
        assertFindByPrisonerNameIsAsExpected("double", 3)
        assertFindByPrisonerNameIsAsExpected("thRee", 3)
        assertFindByPrisonerNameIsAsExpected("haLF", 3)
      }

      @Test
      fun `retrieve relevant applications, when searching with jobTitleOrEmployerName`() {
        assertFindByJobTitleOrEmployerNameIsAsExpected(
          jobTitleOrEmployerName = "Amazon",
          expectedSize = 4,
          expectedEmployerName = setOf(amazon.name),
        )
        assertFindByJobTitleOrEmployerNameIsAsExpected(
          jobTitleOrEmployerName = "Tesco",
          expectedSize = 3,
          expectedEmployerName = setOf(tesco.name),
        )
        assertFindByJobTitleOrEmployerNameIsAsExpected(
          jobTitleOrEmployerName = "Abc",
          expectedSize = 2,
          expectedEmployerName = setOf(abcConstruction.name),
        )

        assertFindByJobTitleOrEmployerNameIsAsExpected(
          jobTitleOrEmployerName = "Operator",
          expectedSize = 4,
          expectedJobTitle = setOf(amazonForkliftOperator.title),
        )
        assertFindByJobTitleOrEmployerNameIsAsExpected(
          jobTitleOrEmployerName = "Warehouse",
          expectedSize = 3,
          expectedJobTitle = setOf(tescoWarehouseHandler.title),
        )
        assertFindByJobTitleOrEmployerNameIsAsExpected(
          jobTitleOrEmployerName = "Apprentice",
          expectedSize = 2,
          expectedJobTitle = setOf(abcConstructionApprentice.title),
        )
      }

      @Test
      fun `retrieve relevant applications, when searching with jobTitleOrEmployerName of mixed cases`() {
        assertFindByJobTitleOrEmployerNameIsAsExpected("amazon", 4)
        assertFindByJobTitleOrEmployerNameIsAsExpected("TESCO", 3)
        assertFindByJobTitleOrEmployerNameIsAsExpected("aBc", 2)

        assertFindByJobTitleOrEmployerNameIsAsExpected("operator", 4)
        assertFindByJobTitleOrEmployerNameIsAsExpected("WAREHOUSE", 3)
        assertFindByJobTitleOrEmployerNameIsAsExpected("aPPrenTICE", 2)
      }

      private fun assertFindByStatusIsAsExpected(
        applicationStatus: List<String>,
        expectedSize: Int,
      ) {
        val actual = applicationRepository.findByPrisonIdAndPrisonerNameAndApplicationStatusAndJobTitleOrEmployerName(
          prisonId = prisonId,
          prisonerName = null,
          status = applicationStatus,
          jobTitleOrEmployerName = null,
          pageable = defaultPageable,
        )
        assertThat(actual).isNotEmpty.hasSize(expectedSize)
        actual.content.forEach {
          assertThat(it.status).isIn(applicationStatus)
        }
      }

      private fun assertFindByPrisonerNameIsAsExpected(
        prisonerName: String,
        expectedSize: Int,
        expectedApplicantNames: Set<String>? = null,
      ) {
        val actual = applicationRepository.findByPrisonIdAndPrisonerNameAndApplicationStatusAndJobTitleOrEmployerName(
          prisonId = prisonId,
          prisonerName = prisonerName,
          status = null,
          jobTitleOrEmployerName = null,
          pageable = defaultPageable,
        )

        assertThat(actual).isNotEmpty.hasSize(expectedSize)

        expectedApplicantNames?.let {
          val applicantFullNames = actual.map { "${it.firstName ?: ""} ${it.lastName ?: ""}".trim() }.toSet()
          it.forEach { applicantName ->
            assertThat(applicantName).isIn(applicantFullNames)
          }
        }
      }

      private fun assertFindByJobTitleOrEmployerNameIsAsExpected(
        jobTitleOrEmployerName: String,
        expectedSize: Int,
        expectedEmployerName: Set<String>? = null,
        expectedJobTitle: Set<String>? = null,
      ) {
        val actual = applicationRepository.findByPrisonIdAndPrisonerNameAndApplicationStatusAndJobTitleOrEmployerName(
          prisonId = prisonId,
          prisonerName = null,
          status = null,
          jobTitleOrEmployerName = jobTitleOrEmployerName,
          pageable = defaultPageable,
        )

        assertThat(actual).isNotEmpty.hasSize(expectedSize)

        expectedJobTitle?.let { actual.forEach { assertThat(it.job.title).isIn(expectedJobTitle) } }

        expectedEmployerName?.let { actual.forEach { assertThat(it.job.employer.name).isIn(expectedEmployerName) } }
      }
    }
  }

//  private fun assertRevisionMetadata(
//    expectedRevisionType: RevisionType,
//    expectedCreator: String?,
//    vararg revisions: Revision<Long, Application>,
//  ) {
//    revisions.forEach { revision ->
//      with(revision.metadata) {
//        assertThat(revisionType).isEqualTo(expectedRevisionType)
//        expectedCreator?.let { assertThat(getDelegate<RevisionInfo>().createdBy).isEqualTo(expectedCreator) }
//      }
//    }
//  }

  private fun assertEquals(actual: Application, expected: Application) {
    assertThat(actual).usingRecursiveComparison()
      .ignoringFields("job", "createdBy", "createdAt", "lastModifiedBy", "lastModifiedAt")
      .isEqualTo(expected)
    assertThat(actual.job.id).isEqualTo(expected.job.id)
  }

  override fun setCurrentAuditor(username: String) {
    super.setCurrentAuditor(username)
    this.currentAuditor = username
  }
}
