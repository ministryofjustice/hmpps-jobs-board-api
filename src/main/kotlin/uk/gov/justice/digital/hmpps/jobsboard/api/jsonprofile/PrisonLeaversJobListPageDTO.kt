package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile

import org.springframework.data.domain.Pageable
import org.springframework.hateoas.RepresentationModel

open class PrisonLeaversJobListPageDTO(

  var prisonLeaversJobList: MutableList<PrisonLeaversJobDTO>,
  var total: Int,
  var pageRequest: Pageable,
) : RepresentationModel<PrisonLeaversJobListPageDTO>()
