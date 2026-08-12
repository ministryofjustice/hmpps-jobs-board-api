package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApplicationDTO(
  @field:Schema(description = "The job title of the job applied for", example = "Delivery Driver")
  val jobTitle: String,
  @field:Schema(description = "The employer of the job applied for", example = "Amazon Flex")
  val employerName: String,
  @field:Schema(description = "The prisonNumber of the person who applied for the job", example = "A1234BC")
  val prisonNumber: String,
  @field:Schema(description = "The job applicants first name", example = "Given")
  val firstName: String?,
  @field:Schema(description = "The job applicants last name", example = "Nobody")
  val lastName: String?,
  @field:Schema(description = "The status of the job application", example = "SELECTED_FOR_INTERVIEW")
  val status: String?,
  @field:Schema(description = "Additional information of the job application")
  val additionalInformation: String?,
  @field:Schema(description = "The identifier of the prison where the job application was made", example = "MDI")
  val prisonId: String?,
  @field:Schema(description = "A list of applications made in the past")
  val histories: List<HistoriesDTO>,
  @field:Schema(description = "The user who created this job application", example = "USER1_GEN")
  val createdBy: String,
  @field:Schema(description = "The initial time the job application was made", example = "2024-11-15T18:45:29.916505Z")
  val createdAt: String?,
  @field:Schema(description = "The user who last modified this job application", example = "USER2_GEN")
  val lastModifiedBy: String,
  @field:Schema(description = "The last time the job application was modified", example = "2024-11-25T09:45:29.916505Z")
  val lastModifiedAt: String?,
)
