package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class OsPlacesApiAddress(
  @JsonProperty("DPA")
  val dpa: OsPlacesApiDPA,
)
