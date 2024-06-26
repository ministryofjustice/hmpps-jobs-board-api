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
  constructor(simplifiedPrisonLeaversJob: PrisonLeaversJob) : this(
    id = simplifiedPrisonLeaversJob.id,
    employerId = simplifiedPrisonLeaversJob.employer?.id,
    salaryPeriodName = simplifiedPrisonLeaversJob.salaryPeriodName,
    workPatternName = simplifiedPrisonLeaversJob.workPatternName,
    hoursName = simplifiedPrisonLeaversJob.hoursName,
    jobContractName = simplifiedPrisonLeaversJob.jobContractName,
    jobTypeName = simplifiedPrisonLeaversJob.jobTypeName,
    jobContractId = simplifiedPrisonLeaversJob.jobContractId,
    sectorName = simplifiedPrisonLeaversJob.sectorName,
    additionalSalaryInformation = simplifiedPrisonLeaversJob.additionalSalaryInformation,
    desirableJobCriteria = simplifiedPrisonLeaversJob.desirableJobCriteria,
    essentialJobCriteria = simplifiedPrisonLeaversJob.essentialJobCriteria,
    closingDate = simplifiedPrisonLeaversJob.closingDate,
    howToApply = simplifiedPrisonLeaversJob.howToApply,
    jobTitle = simplifiedPrisonLeaversJob.jobTitle,
    createdBy = simplifiedPrisonLeaversJob.createdBy,
    createdDateTime = simplifiedPrisonLeaversJob.createdDateTime,
    postingDate = simplifiedPrisonLeaversJob.postingDate,
    deletedBy = simplifiedPrisonLeaversJob.deletedBy,
    deletedDateTime = simplifiedPrisonLeaversJob.deletedDateTime,
    modifiedBy = simplifiedPrisonLeaversJob.modifiedBy,
    modifiedDateTime = simplifiedPrisonLeaversJob.modifiedDateTime,
    nationalMinimumWage = simplifiedPrisonLeaversJob.nationalMinimumWage,
    postCode = simplifiedPrisonLeaversJob.postCode,
    ringFencedJob = simplifiedPrisonLeaversJob.ringFencedJob,
    rollingJobOppurtunity = simplifiedPrisonLeaversJob.rollingJobOppurtunity,
    activeJob = simplifiedPrisonLeaversJob.activeJob,
    deletedJob = simplifiedPrisonLeaversJob.deletedJob,
    salaryFrom = simplifiedPrisonLeaversJob.salaryFrom,
    salaryTo = simplifiedPrisonLeaversJob.salaryTo,
    typeOfWork = simplifiedPrisonLeaversJob.typeOfWork,
  )
}
