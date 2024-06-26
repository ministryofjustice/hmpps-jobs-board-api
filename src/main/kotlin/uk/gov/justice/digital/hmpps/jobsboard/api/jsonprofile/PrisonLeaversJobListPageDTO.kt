package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import org.springframework.hateoas.RepresentationModel

open class PrisonLeaversJobListPageDTO(

  var prisonLeaversJobList: MutableList<PrisonLeaversJobSummaryDTO>,
  var total: Int,
) : RepresentationModel<PrisonLeaversJobListPageDTO>()
