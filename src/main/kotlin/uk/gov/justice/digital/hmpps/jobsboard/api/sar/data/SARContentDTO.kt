package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

data class SARContentDTO(
  @param:Schema(description = "List of applications", example = "[]")
  val applications: List<ApplicationDTO?> = emptyList(),
  @param:Schema(description = "List of jobs with an expression of interest", example = "[]")
  val expressionsOfInterest: List<ExpressionOfInterestDTO> = emptyList(),
  @param:Schema(description = "List of archived jobs", example = "[]")
  val archivedJobs: List<ArchivedDTO> = emptyList(),
)
