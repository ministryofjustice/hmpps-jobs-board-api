package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

data class ArchivedDTO(
  @field:Schema(description = "The job title of the archived job", example = "Delivery Driver")
  val jobTitle: String,
  @field:Schema(description = "The employer of the archived job ", example = "Amazon Flex")
  val employerName: String,
  @field:Schema(description = "The prisonNumber of the person having the archived job for", example = "A1234BC")
  val prisonNumber: String,
  @field:Schema(description = "The user who archived this job for the person", example = "USER1_GEN")
  val createdBy: String?,
  @field:Schema(description = "The initial time the job archived for the person ", example = "2024-11-15T18:45:29.916505Z")
  val createdAt: String?,
)
