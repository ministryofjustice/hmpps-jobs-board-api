package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response containing summary for subject access request")
data class SARSummaryDTO(
  val content: SARContentDTO = SARContentDTO(),
)

data class SARContentDTO(
  @Schema(description = "List of applications", example = "[]")
  val applications: List<ApplicationDTO> = emptyList(),
  @Schema(description = "List of jobs with an expression of interest", example = "[]")
  val expressionsOfInterest: List<ExpressionOfInterestDTO> = emptyList(),
  @Schema(description = "List of archived jobs", example = "[]")
  val archivedJobs: List<ArchivedDTO> = emptyList(),
)
