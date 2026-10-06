package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class OsPlacesApiDPA(
  @field:JsonProperty("POSTCODE")
  val postcode: String,

  @field:JsonProperty("X_COORDINATE")
  val xCoordinate: Double?,

  @field:JsonProperty("Y_COORDINATE")
  val yCoordinate: Double?,
)
