package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

data class GetMetricsApplicationCountByStatusResponse(
  val applicationStatus: String,
  val numberOfApplications: Long,
)
