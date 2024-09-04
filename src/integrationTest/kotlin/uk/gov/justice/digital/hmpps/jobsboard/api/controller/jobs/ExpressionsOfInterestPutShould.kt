package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class ExpressionsOfInterestPutShould {
  @Test
  fun `create expression-of-interest with valid job-ID and prisoner's prison-number, when it does NOT exist`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT create expression-of-interest, when it exists`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT create expression-of-interest with non-existent job, and return error`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT create expression-of-interest with invalid prisoner's prison-number, and return error`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT create expression-of-interest without prisoner's prison-number, and return error`() {
    failAsNotImplemented()
  }

  private fun failAsNotImplemented(): Nothing = fail("Yet to implement")
}
