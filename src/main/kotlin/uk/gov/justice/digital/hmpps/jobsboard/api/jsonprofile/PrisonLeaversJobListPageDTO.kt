package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile

import org.springframework.hateoas.RepresentationModel

open class PrisonLeaversJobListPageDTO(

  var prisonLeaversJobList: MutableList<PrisonLeaversJobDTO>,
  var total: Int,
) : RepresentationModel<PrisonLeaversJobListPageDTO>()
