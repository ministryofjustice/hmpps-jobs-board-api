package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import jakarta.validation.constraints.Size

@ConsistentCopyVisibility
data class CreateEmployerRequest internal constructor(
  val id: String = "",
  @field:Size(max = 100, min = 3)
  val name: String,
  @field:Size(max = 1000)
  val description: String,
  val sector: String,
  val status: String,
) {
  companion object {
    fun from(
      id: String,
      name: String,
      description: String,
      sector: String,
      status: String,
    ): CreateEmployerRequest = CreateEmployerRequest(
      id,
      name,
      description,
      sector,
      status,
    )
  }
}
