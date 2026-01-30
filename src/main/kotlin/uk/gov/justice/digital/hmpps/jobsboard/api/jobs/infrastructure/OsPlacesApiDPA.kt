package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class OsPlacesApiDPA(
  @param:JsonProperty("POSTCODE")
  val postcode: String,

  @param:JsonProperty("X_COORDINATE")
  val xCoordinate: Double?,

  @param:JsonProperty("Y_COORDINATE")
  val yCoordinate: Double?,
)
