package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

data class HistoriesDTO(
  @param:Schema(description = "The job applicants first name")
  val firstName: String?,
  @param:Schema(description = "The job applicants last name")
  val lastName: String?,
  @param:Schema(description = "The status of the job application", example = "SELECTED_FOR_INTERVIEW")
  val status: String,
  @param:Schema(description = "The name of the prison where the job application was made", example = "Moorland (HMP & YOI)")
  val prisonId: String,
  @param:Schema(description = "When the job application was modified", example = "2024-11-25T09:45:29.916505Z")
  val modifiedAt: String,
)
