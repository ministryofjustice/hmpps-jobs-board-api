package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import kotlin.test.Test
import kotlin.test.fail

class ExpressionsOfInterestEditorShould {
  @Test
  fun `save with valid Job-ID and Prison-Number, when it does NOT exist`() {
    failAsNotImplemented()
  }

  @Test
  fun `save and return true, when it does NOT exist`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT save expression-of-interest again, and return false, when it exists`() {
    failAsNotImplemented()
  }

  @Test
  fun `save with current time, when it does NOT exist`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when Job-ID is empty at creation`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when Job-ID is invalid at creation`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when job does NOT exist at creation`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when prisoner's Prison-Number is invalid at creation`() {
    failAsNotImplemented()
  }

  @Test
  fun `delete, when it exists`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when Job-ID is empty at deletion`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when Job-ID is invalid at deletion`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when prisoner's Prison-Number is invalid at deletion`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception, when expression-of-interest does NOT exist at deletion`() {
    failAsNotImplemented()
  }

  private fun failAsNotImplemented(): Nothing = fail("Yet to implement")
}
