package uk.gov.justice.digital.hmpps.jobsboard.api.sar.data

import io.swagger.v3.oas.annotations.media.Schema

data class ExpressionOfInterestDTO(
  @param:Schema(description = "The job title of the job with expression of interest", example = "Delivery Driver")
  val jobTitle: String,
  @param:Schema(description = "The employer of the job with expression of interest", example = "Amazon Flex")
  val employerName: String?,
  @param:Schema(description = "The prisonNumber of the person who express interest to the job", example = "A1234BC")
  val prisonNumber: String,
  @param:Schema(description = "The initial time of the expression of interest to the job", example = "2024-11-15T18:45:29.916505Z")
  val createdAt: String?,
)
