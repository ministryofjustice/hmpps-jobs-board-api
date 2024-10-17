package uk.gov.justice.digital.hmpps.jobsboard.api.employers.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EmployerMother.sainsburys
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.anotherUserTestName
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.employerCreationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.employerModificationTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.userTestName
import java.util.*

class EmployerRepositoryShould : EmployerRepositoryTestCase() {
  @Test
  fun `save an Employer`() {
    employerRepository.save(sainsburys)
  }

  @Test
  fun `find an existing Employer`() {
    employerRepository.save(sainsburys)

    assertEquals(
      Optional.of(sainsburys),
      sainsburys.id.let { employerRepository.findById(it) },
    )
  }

  @Test
  fun `not find a non existing Employer`() {
    assertFalse(employerRepository.findById(EntityId("be756fdd-8258-4561-88db-6fbd84295411")).isPresent)
  }

  @Test
  fun `set createdAt attribute when saving a new Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    assertThat(savedEmployer.createdAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `not update createdAt attribute when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerModificationTime))

    val updatedEmployer = employerRepository.save(savedEmployer)

    assertThat(updatedEmployer.createdAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `set modifiedAt attribute with same value as createdAt when saving a new Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    assertThat(savedEmployer.modifiedAt).isEqualTo(employerCreationTime)
  }

  @Test
  fun `update modifiedAt attribute with current date and time when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(employerModificationTime))

    val updatedEmployer = employerRepository.saveAndFlush(savedEmployer)

    assertThat(updatedEmployer.modifiedAt).isEqualTo(employerModificationTime)
  }

  @Test
  fun `set createdBy attribute when saving a new Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    assertThat(savedEmployer.createdBy).isEqualTo(userTestName)
  }

  @Test
  fun `not update createdBy attribute when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)
    whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(anotherUserTestName))

    employerRepository.saveAndFlush(savedEmployer)

    assertThat(savedEmployer.createdBy).isEqualTo(userTestName)
  }

  @Test
  fun `set lastModifiedBy attribute when saving a new Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)

    assertThat(savedEmployer.lastModifiedBy).isEqualTo(userTestName)
  }

  @Test
  fun `update lastModifiedBy attribute when updating an existing Employer`() {
    val savedEmployer = employerRepository.save(sainsburys)
    whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(anotherUserTestName))

    employerRepository.saveAndFlush(savedEmployer)

    assertThat(savedEmployer.lastModifiedBy).isEqualTo(anotherUserTestName)
  }
}
