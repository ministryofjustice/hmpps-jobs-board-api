package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class ExpressionsOfInterestDeleteShould {
  @Test
  fun `delete expression-of-interest, when it exists`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT delete expression-of-interest, when it does NOT exist, and return error`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT delete expression-of-interest with non-existent job, and return error`() {
    failAsNotImplemented()
  }

  @Test
  fun `do NOT delete expression-of-interest without prisoner's prison-number, and return error`() {
    failAsNotImplemented()
  }

  private fun failAsNotImplemented(): Nothing = fail("Yet to implement")
}
