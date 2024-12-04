package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

interface MetricsCountByStatus {
  val status: String
  val count: Long
}
