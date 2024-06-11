package uk.gov.justice.digital.hmpps.jobsboard.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HmppsJobsBoardApi

fun main(args: Array<String>) {
  runApplication<HmppsJobsBoardApi>(*args)
}
