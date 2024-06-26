package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import java.time.Instant
import java.time.LocalDateTime

class PrisonLeaversJobDTO(

  var id: Long?,

  var employerId: Long?,

  var salaryPeriodName: SalaryPeriod?,

  var workPatternName: ContractHours?,

  var hoursName: Hours?,

  var jobContractName: String?,

  var jobTypeName: String?,

  var jobContractId: Long?,

  var sectorName: String?,

  var additionalSalaryInformation: String?,

  var desirableJobCriteria: String?,

  var essentialJobCriteria: String?,

  var closingDate: LocalDateTime?,

  var howToApply: String?,

  var jobTitle: String?,

  var createdBy: String?,

  var createdDateTime: Instant?,

  var postingDate: String?,

  var deletedBy: String?,

  var deletedDateTime: Instant?,

  var modifiedBy: String?,

  var modifiedDateTime: Instant?,

  var nationalMinimumWage: Boolean?,

  var postCode: String?,

  var ringFencedJob: Boolean?,

  var rollingJobOppurtunity: Boolean?,

  var activeJob: Boolean?,

  var deletedJob: Boolean?,

  var salaryFrom: String?,

  var salaryTo: String?,

  var typeOfWork: TypeOfWork?,

) {
  constructor(prisonLeaversJob: PrisonLeaversJob) : this(
    id = prisonLeaversJob.id,
    employerId = prisonLeaversJob.employer?.id,
    salaryPeriodName = prisonLeaversJob.salaryPeriodName,
    workPatternName = prisonLeaversJob.workPatternName,
    hoursName = prisonLeaversJob.hoursName,
    jobContractName = prisonLeaversJob.jobContractName,
    jobTypeName = prisonLeaversJob.jobTypeName,
    jobContractId = prisonLeaversJob.jobContractId,
    sectorName = prisonLeaversJob.sectorName,
    additionalSalaryInformation = prisonLeaversJob.additionalSalaryInformation,
    desirableJobCriteria = prisonLeaversJob.desirableJobCriteria,
    essentialJobCriteria = prisonLeaversJob.essentialJobCriteria,
    closingDate = prisonLeaversJob.closingDate,
    howToApply = prisonLeaversJob.howToApply,
    jobTitle = prisonLeaversJob.jobTitle,
    createdBy = prisonLeaversJob.createdBy,
    createdDateTime = prisonLeaversJob.createdDateTime,
    postingDate = prisonLeaversJob.postingDate,
    deletedBy = prisonLeaversJob.deletedBy,
    deletedDateTime = prisonLeaversJob.deletedDateTime,
    modifiedBy = prisonLeaversJob.modifiedBy,
    modifiedDateTime = prisonLeaversJob.modifiedDateTime,
    nationalMinimumWage = prisonLeaversJob.nationalMinimumWage,
    postCode = prisonLeaversJob.postCode,
    ringFencedJob = prisonLeaversJob.ringFencedJob,
    rollingJobOppurtunity = prisonLeaversJob.rollingJobOppurtunity,
    activeJob = prisonLeaversJob.activeJob,
    deletedJob = prisonLeaversJob.deletedJob,
    salaryFrom = prisonLeaversJob.salaryFrom,
    salaryTo = prisonLeaversJob.salaryTo,
    typeOfWork = prisonLeaversJob.typeOfWork,
  )
}
