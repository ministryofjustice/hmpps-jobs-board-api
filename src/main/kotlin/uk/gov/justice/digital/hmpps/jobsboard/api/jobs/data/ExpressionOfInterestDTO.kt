package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.data

data class ExpressionOfInterestDTO(
  val jobTitle: String,
  val employerName: String?,
  val prisonNumber: String,
  val createdAt: String?,
)
