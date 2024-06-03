package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.PrisonLeaversJob

@Relation(collectionRelation = "prisonLeaversJobDTO")
open class PrisonLeaversJobDTO(

  var employerName: String,
  var jobTitle: String,
  var closingDate: String,
  var distance: Long,
  var city: String,
  var postcode: String,
  var typeOfWork: String,
) : RepresentationModel<PrisonLeaversJobDTO>()
