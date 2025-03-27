package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import java.time.LocalDate

data class SARFilter(
  var prn: String,
  val fromDate: LocalDate?,
  val toDate: LocalDate?,
)
