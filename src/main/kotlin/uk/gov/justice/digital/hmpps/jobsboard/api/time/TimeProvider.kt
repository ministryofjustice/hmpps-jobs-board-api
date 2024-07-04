package uk.gov.justice.digital.hmpps.jobsboard.api.time

import java.time.LocalDateTime

interface TimeProvider {
  fun now(): LocalDateTime
}
