package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

data class ArchivedDTO(
  val jobTitle: String,
  val employerName: String,
  val prisonNumber: String,
  val createdAt: String?,
)
