package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.RepresentationModel
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ExcludingOffences
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import java.time.LocalDateTime

open class PrisonLeaversJobDetailDTO(

  var employerName: String?,
  var jobTitle: String?,
  var closingDate: LocalDateTime?,
  var distance: Long?,
  var city: String?,
  var postcode: String?,
  var typeOfWork: TypeOfWork?,
  var salaryFrom: String?,
  var salaryTo: String?,
  var salaryPeriod: SalaryPeriod?,
  var offenceExclusions: ExcludingOffences?,
  var essentialCriteria: String?,
  var jobDescription: String?,
  var workPattern: ContractHours?,
  var additionalSalaryInformation: String?,
  var desirableCriteria: String?,
  var hours: Hours?,

) : RepresentationModel<PrisonLeaversJobDetailDTO>() {
  constructor(prisonLeaversJob: PrisonLeaversJob) : this(
    employerName = prisonLeaversJob.employer?.employerName,
    jobTitle = prisonLeaversJob.jobTitle,
    closingDate = prisonLeaversJob.closingDate,
    distance = prisonLeaversJob.distance,
    city = "city",
    postcode = prisonLeaversJob.employer?.postCode,
    typeOfWork = prisonLeaversJob.typeOfWork,
    salaryFrom = prisonLeaversJob.salaryFrom,
    salaryTo = prisonLeaversJob.salaryTo,
    salaryPeriod = prisonLeaversJob.salaryPeriod?.mnSalaryPeriodName,
    offenceExclusions = null,
    essentialCriteria = prisonLeaversJob.essentialJobCriteria,
    jobDescription = null,
    workPattern = prisonLeaversJob.workPattern?.mnWorkPatternName,
    additionalSalaryInformation = prisonLeaversJob.additionalSalaryInformation,
    desirableCriteria = prisonLeaversJob.desirableJobCriteria,
    hours = prisonLeaversJob.hoursType?.mnHoursName,
  )
}
