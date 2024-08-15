package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

data class GetJobsResponse(
  val id: String,
  val employerId: String,
  val employerName: String,
  val jobTitle: String,
  val numberOfVacancies: Int,
  val sector: String,
  val createdAt: String,
) {
  companion object {
    fun from(job: Job): GetJobsResponse {
      return GetJobsResponse(
        id = job.id.toString(),
        employerId = job.employer.id.id,
        employerName = job.employer.name,
        jobTitle = job.title,
        numberOfVacancies = job.numberOfVacancies,
        sector = job.sector,
        createdAt = job.createdAt.toString(),
      )
    }
  }
}
