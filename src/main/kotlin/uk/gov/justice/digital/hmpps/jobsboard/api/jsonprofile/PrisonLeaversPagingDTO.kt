package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import uk.gov.justice.digital.hmpps.jobsboard.api.enums.PrisonLeaversJobSort
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork

data class PrisonLeaversPagingDTO(

  var sort: PrisonLeaversJobSort,
  var pageNo: Int,
  var pageSize: Int,
  var typeOfWork: TypeOfWork?,
  var postCode: String?,
  var distance: Long?,
)
