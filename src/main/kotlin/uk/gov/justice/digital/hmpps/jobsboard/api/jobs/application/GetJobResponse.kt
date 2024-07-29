package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

data class GetJobResponse(
  val id: String,
  val employerId: String,
  val jobTitle: String,
  val sector: String,
  val industrySector: String,
  val numberOfVacancies: String,
  val sourcePrimary: String,
  val sourceSecondary: String? = null,
  val charityName: String? = null,
  val postCode: String,
  val salaryFrom: String,
  val salaryTo: String? = null,
  val salaryPeriod: String,
  val additionalSalaryInformation: String? = null,
  val isPayingAtLeastNationalMinimumWage: Boolean,
  val workPattern: String,
  val hoursPerWeek: String,
  val contractType: String,
  val baseLocation: String,
  val essentialCriteria: String,
  val desirableCriteria: String? = null,
  val description: String,
  val offenceExclusions: List<String>,
  val isRollingOpportunity: Boolean,
  val closingDate: String? = null,
  val isOnlyForPrisonLeavers: Boolean,
  val startDate: String? = null,
  val howToApply: String,
  val supportingDocumentationRequired: List<String>,
  val supportingDocumentationDetails: String? = null,
) {
  companion object {
    fun from(job: Job): GetJobResponse {
      return GetJobResponse(
        id = job.id.toString(),
        employerId = job.employer.id.id,
        jobTitle = job.title,
        sector = job.sector,
        industrySector = job.industrySector,
        numberOfVacancies = job.numberOfVacancies,
        sourcePrimary = job.sourcePrimary,
        sourceSecondary = job.sourceSecondary,
        charityName = job.charityName,
        postCode = job.postCode,
        salaryFrom = job.salaryFrom,
        salaryTo = job.salaryTo,
        salaryPeriod = job.salaryPeriod,
        additionalSalaryInformation = job.additionalSalaryInformation,
        isPayingAtLeastNationalMinimumWage = job.isPayingAtLeastNationalMinimumWage,
        workPattern = job.workPattern,
        hoursPerWeek = job.hoursPerWeek,
        contractType = job.contractType,
        baseLocation = job.baseLocation,
        essentialCriteria = job.essentialCriteria,
        desirableCriteria = job.desirableCriteria,
        description = job.description,
        offenceExclusions = job.offenceExclusions.asList(),
        isRollingOpportunity = job.isRollingOpportunity,
        closingDate = job.closingDate.toString(),
        isOnlyForPrisonLeavers = job.isOnlyForPrisonLeavers,
        startDate = job.startDate.toString(),
        howToApply = job.howToApply,
        supportingDocumentationRequired = job.supportingDocumentationRequired.asList(),
        supportingDocumentationDetails = job.supportingDocumentationDetails,
      )
    }
    private fun String.asList(): List<String> {
      return this.split(",").map { it.trim() }.toList()
    }
  }
}
