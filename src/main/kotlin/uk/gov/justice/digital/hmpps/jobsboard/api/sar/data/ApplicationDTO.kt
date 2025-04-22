package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

data class ApplicationDTO(
  @Schema(description = "The job title of the job applied for", example = "Delivery Driver")
  val jobTitle: String,
  @Schema(description = "The employer of the job applied for", example = "Amazon Flex")
  val employerName: String,
  @Schema(description = "The prisonNumber of the person who applied for the job", example = "A1234BC")
  val prisonNumber: String,
  @Schema(description = "The job applicants first name", example = "John")
  val firstName: String?,
  @Schema(description = "The job applicants last name", example = "Smith")
  val lastName: String?,
  @Schema(description = "The status of the job application", example = "SELECTED_FOR_INTERVIEW")
  val status: String?,
  @Schema(description = "The name of the prison where the job application was made", example = "Moorland (HMP & YOI)")
  val prisonId: String?,
  @Schema(description = "A list of applications made in the past")
  val histories: List<HistoriesDTO>,
  @Schema(description = "The initial time the job application was made", example = "2024-11-15T18:45:29.916505Z")
  val createdAt: String?,
  @Schema(description = "The last time the job application was modified", example = "2024-11-25T09:45:29.916505Z")
  val lastModifiedAt: String?,
)
