package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import java.time.LocalDateTime

data class CreateEmployerRequest internal constructor(
  val id: String = "",
  val name: String,
  val description: String,
  val createdBy: String?,
  val createdWhen: LocalDateTime?,
  val modifiedBy: String?,
  val modifiedWhen: LocalDateTime?,
  val sector: String,
  val status: String,
) {
  companion object {
    fun from(
      id: String,
      name: String,
      description: String,
      createdBy: String?,
      createdWhen: LocalDateTime?,
      modifiedBy: String?,
      modifiedWhen: LocalDateTime?,
      sector: String,
      status: String,
    ): CreateEmployerRequest {
      return CreateEmployerRequest(
        id,
        name,
        description,
        createdBy,
        createdWhen,
        modifiedBy,
        modifiedWhen,
        sector,
        status,
      )
    }
  }
}
