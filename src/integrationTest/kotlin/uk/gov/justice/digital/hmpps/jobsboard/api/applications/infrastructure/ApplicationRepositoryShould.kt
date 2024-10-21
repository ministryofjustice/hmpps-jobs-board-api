package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.data.history.Revision
import org.springframework.data.history.RevisionMetadata.RevisionType
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.audit.domain.RevisionInfo
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications.ApplicationMother
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.util.*

class ApplicationRepositoryShould : ApplicationRepositoryTestCase() {
  private val firstAuditor = ApplicationMother.createdBy
  private val subsequentAuditor = ApplicationMother.lastModifiedBy

  private var currentAuditor = firstAuditor

  @BeforeEach
  override fun setUp() {
    super.setUp()
    setCurrentAuditor(currentAuditor)
  }

  @AfterAll
  fun cleanUpAll() {
    this.applicationRepository.deleteAll()
    this.jobRepository.deleteAll()
    this.employerRepository.deleteAll()
  }

  @Test
  fun `return false, when checking existence of non-existent application`() {
    val jobIdNotExist = EntityId(UUID.randomUUID().toString())
    val exist = applicationRepository.existsById(jobIdNotExist)
    assertThat(exist).isFalse()
  }

  @Nested
  @DisplayName("Given the known application")
  inner class GivenTheKnownApplicant {
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
        val application = ApplicationMother.builder().apply { job = givenAJobHasBeenCreated() }.build()
        this.application = applicationRepository.saveAndFlush(application)
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

      @Test
      fun `retrieve the latest revision of the application`() {
        val revisions = applicationRepository.findRevisions(application.id)

        assertThat(revisions).isNotEmpty
        revisions.latestRevision.let { latest ->
          with(latest.metadata) {
            assertThat(revisionType).isEqualTo(RevisionType.INSERT)
            assertThat(getDelegate<RevisionInfo>().createdBy).isEqualTo(currentAuditor)
          }
          assertEquals(latest.entity, this.application)
          with(latest.entity) {
            assertThat(createdBy).isNotNull
            assertThat(lastModifiedBy).isNotNull
            assertThat(createdAt).isNotNull
            assertThat(lastModifiedAt).isNotNull
          }
        }
      }

      @Test
      fun `retrieve all revisions of the application, when it has been updated multiple times`() {
        setCurrentAuditor(subsequentAuditor)
        val updateCount = 3
        repeat(updateCount) { index ->
          ApplicationBuilder().from(application).apply {
            additionalInformation = "updating info: ${index + 1}"
          }.build().let {
            application = applicationRepository.saveAndFlush(it)
          }
        }
        val expectedRevisionCount = updateCount + 1

        val revisions = applicationRepository.findRevisions(application.id)
        assertThat(revisions).isNotEmpty
        assertThat(revisions.content.count()).isEqualTo(expectedRevisionCount)
        assertRevisionMetadata(RevisionType.UPDATE, currentAuditor, revisions.latestRevision)
        assertRevisionMetadata(RevisionType.INSERT, firstAuditor, revisions.content[0])
        assertRevisionMetadata(
          expectedRevisionType = RevisionType.UPDATE,
          expectedCreator = subsequentAuditor,
          revisions = revisions.content.subList(1, revisions.content.size).toTypedArray(),
        )
      }
    }
  }

  private fun assertRevisionMetadata(
    expectedRevisionType: RevisionType,
    expectedCreator: String?,
    vararg revisions: Revision<Long, Application>,
  ) {
    revisions.forEach { revision ->
      with(revision.metadata) {
        assertThat(revisionType).isEqualTo(expectedRevisionType)
        expectedCreator?.let { assertThat(getDelegate<RevisionInfo>().createdBy).isEqualTo(expectedCreator) }
      }
    }
  }

  private fun assertEquals(actual: Application, expected: Application) {
    assertThat(actual).usingRecursiveComparison()
      .ignoringFields("job", "createdBy", "createdAt", "lastModifiedBy", "lastModifiedAt")
      .isEqualTo(expected)
    assertThat(actual.job.id).isEqualTo(expected.job.id)
  }

  private fun setCurrentAuditor(username: String) {
    this.currentAuditor = username
    whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(username))
  }
}
