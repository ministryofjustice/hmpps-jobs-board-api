package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import jakarta.validation.constraints.Size

@ConsistentCopyVisibility
data class CreateEmployerRequest internal constructor(
  val id: String = "",
  val name: String,
  @field:Size(max = 500)
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
    ): CreateEmployerRequest {
      return CreateEmployerRequest(
        id,
        name,
        description,
        sector,
        status,
      )
    }
  }
}
