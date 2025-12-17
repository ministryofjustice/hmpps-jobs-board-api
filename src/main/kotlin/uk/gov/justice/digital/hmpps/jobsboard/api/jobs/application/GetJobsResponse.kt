package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

data class GetJobsResponse(
  val id: String,
  val employerId: String,
  val employerName: String,
  val jobTitle: String,
  val numberOfVacancies: Int,
  val sector: String,
  val closingDate: String? = null,
  val createdAt: String,
  val createdBy: String,
) {
  companion object {
    fun from(job: Job): GetJobsResponse = GetJobsResponse(
      id = job.id.toString(),
      employerId = job.employer.id.id,
      employerName = job.employer.name,
      jobTitle = job.title,
      numberOfVacancies = job.numberOfVacancies,
      sector = job.sector,
      closingDate = job.closingDate?.toString(),
      createdAt = job.createdAt.toString(),
      createdBy = job.createdBy!!,
    )
  }
}
