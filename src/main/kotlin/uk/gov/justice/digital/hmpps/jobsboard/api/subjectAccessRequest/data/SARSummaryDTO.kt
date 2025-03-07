package uk.gov.justice.digital.hmpps.jobsboard.api.subjectAccessRequest.data

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.data.ApplicationDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.data.ExpressionOfInterestDTO

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
