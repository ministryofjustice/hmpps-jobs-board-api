package uk.gov.justice.digital.hmpps.jobsboard.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
class HmppsJobsBoardApi

fun main(args: Array<String>) {
  runApplication<HmppsJobsBoardApi>(*args)
}
