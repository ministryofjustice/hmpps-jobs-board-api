package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

data class ArchivedDTO(
  @param:Schema(description = "The job title of the job applied for", example = "Delivery Driver")
  val jobTitle: String,
  @param:Schema(description = "The employer of the job applied for", example = "Amazon Flex")
  val employerName: String,
  @param:Schema(description = "The prisonNumber of the person who applied for the job", example = "A1234BC")
  val prisonNumber: String,
  @param:Schema(description = "The initial time the job application was made", example = "2024-11-15T18:45:29.916505Z")
  val createdAt: String?,
)
