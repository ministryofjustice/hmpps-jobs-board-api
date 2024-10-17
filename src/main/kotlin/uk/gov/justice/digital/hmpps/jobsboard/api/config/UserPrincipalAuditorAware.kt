package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserPrincipalAuditorAware : AuditorAware<String> {
  override fun getCurrentAuditor(): Optional<String> = Optional.of("")
}
