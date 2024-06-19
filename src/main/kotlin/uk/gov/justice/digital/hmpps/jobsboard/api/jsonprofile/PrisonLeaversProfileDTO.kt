package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.server.core.Relation
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversJob

@Relation(collectionRelation = "prisonLeaversJobDTO")
open class PrisonLeaversProfileDTO(

  var offenderId: String?,
  var prisonLeaversJob: PrisonLeaversJob?,
)
