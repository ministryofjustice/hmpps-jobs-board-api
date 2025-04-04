package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer

data class GetEmployerResponse(
  val id: String,
  val name: String,
  val description: String,
  val sector: String,
  val status: String,
  val createdAt: String,
) {
  companion object {
    fun from(employer: Employer): GetEmployerResponse = GetEmployerResponse(
      id = employer.id.toString(),
      name = employer.name,
      description = employer.description,
      sector = employer.sector,
      status = employer.status,
      createdAt = employer.createdAt.toString(),
    )
  }
}
