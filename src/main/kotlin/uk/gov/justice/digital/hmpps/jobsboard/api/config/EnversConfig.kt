package uk.gov.justice.digital.hmpps.jobsboard.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.envers.repository.config.EnableEnversRepositories

@Configuration
@EnableEnversRepositories(basePackages = ["uk.gov.justice.digital.hmpps.jobsboard.api"])
class EnversConfig
