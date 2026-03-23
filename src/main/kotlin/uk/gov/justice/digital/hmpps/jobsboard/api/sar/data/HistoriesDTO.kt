package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

data class HistoriesDTO(
  @field:Schema(description = "The job applicants first name")
  val firstName: String?,
  @field:Schema(description = "The job applicants last name")
  val lastName: String?,
  @field:Schema(description = "The status of the job application", example = "SELECTED_FOR_INTERVIEW")
  val status: String,
  @field:Schema(description = "The identifier of the prison where the job application was made", example = "MDI")
  val prisonId: String,
  @field:Schema(description = "The user who modified this job application", example = "USER2_GEN")
  val modifiedBy: String,
  @field:Schema(description = "When the job application was modified", example = "2024-11-25T09:45:29.916505Z")
  val modifiedAt: String,
)
