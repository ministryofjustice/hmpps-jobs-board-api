package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = "prisonLeaversJobDTO")
open class PrisonLeaversProfileAndJobDTO(

  var offenderId: String?,
  var prisonLeaversJobId: Long?,
)
