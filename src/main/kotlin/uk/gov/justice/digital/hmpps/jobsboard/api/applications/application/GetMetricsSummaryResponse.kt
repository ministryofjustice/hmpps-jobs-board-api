package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

data class GetMetricsSummaryResponse(
  val numberOfApplicants: Long,
  val numberOfJobs: Long,
)
