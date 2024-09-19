package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

data class GetMatchingCandidateJobResponse(
  val id: String,
  val employerName: String,
  val jobTitle: String,
  val closingDate: String?,
  val startDate: String?,
  val postcode: String,
  val distance: Float?,
  val sector: String,
  val salaryFrom: Float,
  val salaryTo: Float?,
  val salaryPeriod: String,
  val additionalSalaryInformation: String?,
  val workPattern: String,
  val hoursPerWeek: String,
  val contractType: String,
  val offenceExclusions: List<String>,
  val essentialCriteria: String,
  val desirableCriteria: String?,
  val description: String,
  val howToApply: String,
  val expressionOfInterest: Boolean,
  val archived: Boolean,
  val createdAt: String,
) {
  companion object {
    fun from(
      job: Job,
      expressionOfInterest: Boolean = false,
      archived: Boolean = false,
    ) = GetMatchingCandidateJobResponse(
      id = job.id.id,
      employerName = job.employer.name,
      jobTitle = job.title,
      closingDate = job.closingDate?.toString(),
      startDate = job.startDate?.toString(),
      postcode = job.postcode,
      distance = null,
      sector = job.sector,
      salaryFrom = job.salaryFrom,
      salaryTo = job.salaryTo,
      salaryPeriod = job.salaryPeriod,
      additionalSalaryInformation = job.additionalSalaryInformation,
      workPattern = job.workPattern,
      hoursPerWeek = job.hoursPerWeek,
      contractType = job.contractType,
      offenceExclusions = job.offenceExclusions.split(","),
      essentialCriteria = job.essentialCriteria,
      desirableCriteria = job.desirableCriteria,
      description = job.description,
      howToApply = job.howToApply,
      expressionOfInterest = expressionOfInterest,
      archived = archived,
      createdAt = job.createdAt.toString(),
    )
  }
}
