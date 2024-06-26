package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import java.time.LocalDateTime

@Relation(collectionRelation = "prisonLeaversJobDTO")
open class PrisonLeaversJobSummaryDTO(

  var employerName: String?,
  var jobTitle: String?,
  var closingDate: LocalDateTime?,
  var distance: Long?,
  var city: String?,
  var postcode: String?,
  var typeOfWork: TypeOfWork?,
) : RepresentationModel<PrisonLeaversJobSummaryDTO>() {
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
