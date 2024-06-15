package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork

@Relation(collectionRelation = "prisonLeaversJobDTO")
open class PrisonLeaversJobDTO(

  var employerName: String?,
  var jobTitle: String?,
  var closingDate: String?,
  var distance: Long?,
  var city: String?,
  var postcode: String?,
  var typeOfWork: TypeOfWork?,
) : RepresentationModel<PrisonLeaversJobDTO>() {
  constructor(prisonLeaversJob: PrisonLeaversJob) : this(
    employerName = prisonLeaversJob.employer?.employerName,
    jobTitle = prisonLeaversJob.jobTitle,
    closingDate = prisonLeaversJob.closingDate,
    distance = prisonLeaversJob.distance,
    city = "city",
    postcode = prisonLeaversJob.employer?.postCode,
    typeOfWork = prisonLeaversJob.typeOfWork,
  )
}