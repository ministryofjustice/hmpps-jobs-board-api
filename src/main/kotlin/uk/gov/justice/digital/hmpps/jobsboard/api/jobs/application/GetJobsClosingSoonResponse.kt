package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.time.Instant
import java.time.LocalDate

data class GetJobsClosingSoonResponse(
  val id: String,
  val employerName: String,
  val jobTitle: String,
  val closingDate: String?,
  val sector: String,
  val createdAt: String,
) {
  constructor(
    id: String,
    employerName: String,
    jobTitle: String,
    closingDate: LocalDate?,
    sector: String,
    createdAt: Instant?,
  ) : this(
    id = id,
    employerName = employerName,
    jobTitle = jobTitle,
    closingDate = closingDate?.toString(),
    sector = sector,
    createdAt = createdAt.toString(),
  ) {}

  companion object {
    fun from(job: Job) = GetJobsClosingSoonResponse(
      id = job.id.id,
      employerName = job.employer.name,
      jobTitle = job.title,
      closingDate = job.closingDate?.toString(),
      sector = job.sector,
      createdAt = job.createdAt.toString(),
    )
  }
}
