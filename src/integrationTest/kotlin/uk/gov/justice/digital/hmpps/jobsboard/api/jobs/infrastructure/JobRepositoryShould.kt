package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.amazon
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.anotherUserTestName
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobModificationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.userTestName
import java.util.*

class JobRepositoryShould : JobRepositoryTestCase() {
  @Test
  fun `save a new Job`() {
    employerRepository.save(amazon)
    jobRepository.save(amazonForkliftOperator)
  }

  @Test
  fun `set createdAt attribute when saving a new Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.createdAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `not update createdAt attribute when updating an existing Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.save(amazonForkliftOperator)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobModificationTime))

    val updatedJob = jobRepository.save(savedJob)

    assertThat(updatedJob.createdAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `set lastModifiedAt attribute when saving a new Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.lastModifiedAt).isEqualTo(jobCreationTime)
  }

  @Test
  fun `update lastModifiedAt attribute when updating an existing Job`() {
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobModificationTime))
    employerRepository.save(amazon)
    jobRepository.save(amazonForkliftOperator)

    val updatedJob = jobRepository.saveAndFlush(amazonForkliftOperator)

    assertThat(updatedJob.lastModifiedAt).isEqualTo(jobModificationTime)
  }

  @Test
  fun `set createdBy attribute when saving a new Job`() {
    employerRepository.save(amazon)

    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.createdBy).isEqualTo(userTestName)
  }

  @Test
  fun `not update createdBy attribute when updating an existing Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.save(amazonForkliftOperator)
    whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(anotherUserTestName))

    jobRepository.saveAndFlush(savedJob)

    assertThat(savedJob.createdBy).isEqualTo(userTestName)
  }

  @Test
  fun `set lastModifiedBy attribute when saving a new Job`() {
    employerRepository.save(amazon)

    val savedJob = jobRepository.save(amazonForkliftOperator)

    assertThat(savedJob.lastModifiedBy).isEqualTo(userTestName)
  }

  @Test
  fun `update lastModifiedBy attribute when updating an existing Job`() {
    employerRepository.save(amazon)
    val savedJob = jobRepository.saveAndFlush(amazonForkliftOperator)

    whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(anotherUserTestName))
    val updatedJob = savedJob.copy(
      additionalSalaryInformation = "updated info(1)",
    ).let {
      jobRepository.saveAndFlush(it)
    }.also {
      entityManager.refresh(it)
    }

    assertThat(updatedJob.lastModifiedBy)
      .isEqualTo(anotherUserTestName)
      .isNotEqualTo(updatedJob.createdBy)
  }

  @Nested
  @DisplayName("Given a long username")
  inner class GivenALongUsername {
    private val longUsername = "${"A".repeat(234)}@a.com"

    @BeforeEach
    fun setUp() {
      whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(longUsername))
    }

    @Test
    fun `create new job with a long username`() {
      employerRepository.save(amazon)

      val savedJob = jobRepository.save(amazonForkliftOperator)
      with(savedJob) {
        assertThat(createdBy).hasSize(240)
        assertThat(lastModifiedBy).hasSize(240)
      }
    }
  }
}
