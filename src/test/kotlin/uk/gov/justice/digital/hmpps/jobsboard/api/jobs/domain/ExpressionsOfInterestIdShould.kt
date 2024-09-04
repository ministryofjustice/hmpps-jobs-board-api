package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import kotlin.test.Test
import kotlin.test.fail

class ExpressionsOfInterestIdShould {
  @Test
  fun `create with valid Job-ID and prisoner's Prison-Number`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception with invalid Job-ID`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception with empty Job-ID`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception with null Job-ID`() {
    failAsNotImplemented()
  }

  fun `throw exception with lengthy Prison-Number`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception with empty prisoner's Prison-Number`() {
    failAsNotImplemented()
  }

  @Test
  fun `throw exception with null prisoner's Prison-Number`() {
    failAsNotImplemented()
  }

  private fun failAsNotImplemented(): Nothing = fail("Yet to implement")
}
