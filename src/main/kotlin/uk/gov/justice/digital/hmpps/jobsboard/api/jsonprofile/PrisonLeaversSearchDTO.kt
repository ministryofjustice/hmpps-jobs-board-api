package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.server.core.Relation
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.PrisonLeaversSort
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork

@Relation(collectionRelation = "prisonLeaversJobDTO")
open class PrisonLeaversSearchDTO(

  var offenderId: String?,
  var typeofWorkList: MutableList<TypeOfWork>,
  var prisonLeaversJob: PrisonLeaversSort?,
  var count: Long,
)
