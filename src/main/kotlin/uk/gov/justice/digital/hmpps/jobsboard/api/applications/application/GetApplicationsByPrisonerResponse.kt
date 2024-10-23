package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application

data class GetApplicationsByPrisonerResponse(
  val id: String,
  val jobId: String,
  val employerName: String,
  val jobTitle: String,
  val applicationStatus: String,
  val createdAt: String,
  val lastModifiedAt: String,
) {
  companion object {
    fun from(application: Application) = application.run {
      GetApplicationsByPrisonerResponse(
        id = id.id,
        jobId = job.id.id,
        employerName = job.employer.name,
        jobTitle = job.title,
        applicationStatus = status,
        createdAt = createdAt.toString(),
        lastModifiedAt = lastModifiedAt.toString(),
      )
    }
  }
}
