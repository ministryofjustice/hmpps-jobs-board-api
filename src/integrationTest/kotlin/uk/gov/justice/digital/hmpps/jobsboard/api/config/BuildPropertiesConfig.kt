package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.*

@Configuration
class BuildPropertiesConfig {
  @Bean
  @Primary
  fun buildProperties(): BuildProperties {
    val properties = Properties()
    properties["version"] = "0.1"
    return BuildProperties(Properties())
  }
}
