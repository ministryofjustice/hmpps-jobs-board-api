package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "prisonLeaversJobDTO")
open class PrisonLeaversProfileDTO(

  var offenderId: String?,
  var prisonLeaversJobId: Long?,
)
