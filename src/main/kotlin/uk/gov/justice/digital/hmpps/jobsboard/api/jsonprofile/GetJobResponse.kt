package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import java.time.LocalDateTime

data class GetJobResponse internal constructor(
  var id: String?,

  var employerId: String,

  var salaryPeriodName: SalaryPeriod?,

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
  var distance: Long,
) {

  companion object {
    fun from(Job: Job): GetJobResponse {
      return GetJobResponse(
        id = Job.id.toString(),
        employerId = Job.employer?.id.toString(),
        salaryPeriodName = Job.salaryPeriodName,
        workPatternName = Job.workPatternName,
        hoursName = Job.hoursName,
        additionalSalaryInformation = Job.additionalSalaryInformation,
        desirableJobCriteria = Job.desirableJobCriteria,
        essentialJobCriteria = Job.essentialJobCriteria,
        closingDate = Job.closingDate,
        howToApply = Job.howToApply,
        jobTitle = Job.jobTitle,
        createdBy = Job.createdBy,
        createdDateTime = Job.createdDateTime,
        postingDate = Job.postingDate,
        deletedBy = Job.deletedBy,
        deletedDateTime = Job.deletedDateTime,
        modifiedBy = Job.modifiedBy,
        modifiedDateTime = Job.modifiedDateTime,
        nationalMinimumWage = Job.nationalMinimumWage,
        postCode = Job.postCode,
        city = Job.city,
        ringFencedJob = Job.ringFencedJob,
        rollingJobOppurtunity = Job.rollingJobOppurtunity,
        activeJob = Job.activeJob,
        deletedJob = Job.deletedJob,
        salaryFrom = Job.salaryFrom,
        salaryTo = Job.salaryTo,
        typeOfWork = Job.typeOfWork,
        distance = 0,
      )
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as GetJobResponse

    if (id != other.id) return false
    if (employerId != other.employerId) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + employerId.hashCode()
    return result
  }
}
