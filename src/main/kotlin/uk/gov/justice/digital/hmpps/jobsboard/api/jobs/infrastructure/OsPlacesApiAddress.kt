package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class OsPlacesApiAddress(
  @param:JsonProperty("DPA")
  val dpa: OsPlacesApiDPA,
)
