package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "os.places.api")
class OSPlacesAPIProperties {
  lateinit var url: String
  lateinit var key: String
}
