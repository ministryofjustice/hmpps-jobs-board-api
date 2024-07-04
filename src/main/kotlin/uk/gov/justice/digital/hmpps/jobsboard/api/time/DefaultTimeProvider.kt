package uk.gov.justice.digital.hmpps.jobsboard.api.time

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DefaultTimeProvider : TimeProvider {
  override fun now(): LocalDateTime {
    return LocalDateTime.now()
  }
}
