package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "The content of the subject access request response")
data class SARContentDTO(
  @field:Schema(description = "List of applications")
  val applications: List<ApplicationDTO?> = emptyList(),
  @field:Schema(description = "List of jobs with an expression of interest")
  val expressionsOfInterest: List<ExpressionOfInterestDTO> = emptyList(),
  @field:Schema(description = "List of archived jobs")
  val archivedJobs: List<ArchivedDTO> = emptyList(),
)
