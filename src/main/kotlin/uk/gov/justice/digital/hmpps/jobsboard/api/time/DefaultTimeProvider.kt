package uk.gov.justice.digital.hmpps.jobsboard.api.time

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class DefaultTimeProvider(
  override val timezoneId: ZoneId = ZoneId.systemDefault(),
) : TimeProvider {
  override fun now(): LocalDateTime {
    return LocalDateTime.now()
  }
}
