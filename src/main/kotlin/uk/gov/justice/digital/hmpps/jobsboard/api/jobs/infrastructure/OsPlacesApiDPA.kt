package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class OsPlacesApiDPA(
  @JsonProperty("POSTCODE")
  val postcode: String,

  @JsonProperty("X_COORDINATE")
  val xCoordinate: Double?,

  @JsonProperty("Y_COORDINATE")
  val yCoordinate: Double?,
)
