package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api.integration")
data class IntegrationProperties(
  val enabled: Boolean = false,
)
