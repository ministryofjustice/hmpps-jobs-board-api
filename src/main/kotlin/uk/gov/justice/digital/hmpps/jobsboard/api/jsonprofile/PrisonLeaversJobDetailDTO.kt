package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile

import org.springframework.hateoas.RepresentationModel
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.SalaryPeriod
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.ContractHours
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.ExcludingOffences
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.Hours
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.TypeOfWork

open class PrisonLeaversJobDetailDTO(

  var employerName: String?,
  var jobTitle: String?,
  var closingDate: String?,
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
    salaryPeriod = prisonLeaversJob.salaryPeriod,
    offenceExclusions = null,
    essentialCriteria = prisonLeaversJob.essentialJobCriteria,
    jobDescription = null,
    workPattern = prisonLeaversJob.workPattern?.mnWorkPatternName,
    additionalSalaryInformation = prisonLeaversJob.additionalSalaryInformation,
    desirableCriteria = prisonLeaversJob.desirableJobCriteria,
    hours = prisonLeaversJob.hoursType?.mnHoursName,
  )
}