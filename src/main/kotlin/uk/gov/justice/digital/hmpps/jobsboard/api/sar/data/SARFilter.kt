package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import java.time.Instant

data class SARFilter(
  var prn: String,
  val fromDate: Instant?,
  val toDate: Instant?,
)
