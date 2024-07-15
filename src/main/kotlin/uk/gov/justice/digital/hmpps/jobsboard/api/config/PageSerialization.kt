package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.EnableSpringDataWebSupport

@Configuration
@EnableSpringDataWebSupport(
  pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
class PageSerialization {
}
