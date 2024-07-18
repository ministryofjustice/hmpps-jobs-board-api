package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import java.time.LocalDateTime

data class CreateJobRequest internal constructor(
  var id: String?,

  var employerId: String,

  var salaryPeriodName: SalaryPeriod?,
  var sectorName:String?,

  var workPatternName: ContractHours?,

  var hoursName: Hours?,

  var additionalSalaryInformation: String?,

  var desirableJobCriteria: String?,

  var essentialJobCriteria: String?,

  var closingDate: LocalDateTime?,

  var howToApply: String?,

  var jobTitle: String?,

  var createdBy: String?,

  var createdDateTime: LocalDateTime?,

  var postingDate: String?,

  var deletedBy: String?,

  var deletedDateTime: LocalDateTime?,

  var modifiedBy: String?,

  var modifiedDateTime: LocalDateTime?,

  var nationalMinimumWage: Boolean?,

  var postCode: String?,
  var city: String?,

  var ringFencedJob: Boolean?,

  var rollingJobOppurtunity: Boolean?,

  var activeJob: Boolean?,

  var deletedJob: Boolean?,

  var salaryFrom: String?,

  var salaryTo: String?,

  var typeOfWork: TypeOfWork?,
) {

  companion object {
    fun from(job: Job): CreateJobRequest {
      return CreateJobRequest(
        id = job.id.toString(),
        employerId = job.employer?.id.toString(),
        salaryPeriodName = job.salaryPeriodName,
        workPatternName = job.workPatternName,
        hoursName = job.hoursName,
        sectorName = job.sectorName,
        additionalSalaryInformation = job.additionalSalaryInformation,
        desirableJobCriteria = job.desirableJobCriteria,
        essentialJobCriteria = job.essentialJobCriteria,
        closingDate = job.closingDate,
        howToApply = job.howToApply,
        jobTitle = job.jobTitle,
        createdBy = job.createdBy,
        createdDateTime = job.createdDateTime,
        postingDate = job.postingDate,
        deletedBy = job.deletedBy,
        deletedDateTime = job.deletedDateTime,
        modifiedBy = job.modifiedBy,
        modifiedDateTime = job.modifiedDateTime,
        nationalMinimumWage = job.nationalMinimumWage,
        postCode = job.postCode,
        city = job.city,
        ringFencedJob = job.ringFencedJob,
        rollingJobOppurtunity = job.rollingJobOppurtunity,
        activeJob = job.activeJob,
        deletedJob = job.deletedJob,
        salaryFrom = job.salaryFrom,
        salaryTo = job.salaryTo,
        typeOfWork = job.typeOfWork,
      )
    }
  }
}
