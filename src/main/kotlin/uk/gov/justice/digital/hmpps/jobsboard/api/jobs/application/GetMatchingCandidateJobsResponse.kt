package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

data class GetMatchingCandidateJobsResponse(
  val jobTitle: String,
  val employerName: String,
  val sector: String,
  val postcode: String,
  val distance: Float,
  val closingDate: String? = null,
  val expressionOfInterest: Boolean = false,
  val createdAt: String,
) {
  companion object {
    fun from(job: Job): GetMatchingCandidateJobsResponse {
      return GetMatchingCandidateJobsResponse(
        jobTitle = job.title,
        employerName = job.employer.name,
        sector = job.sector,
        postcode = job.postcode,
        distance = 0f,
        closingDate = job.closingDate?.toString(),
        expressionOfInterest = false,
        createdAt = job.createdAt.toString(),
      )
    }
  }
}
