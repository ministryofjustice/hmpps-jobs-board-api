package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile

import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.PrisonLeaversJobSort
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.TypeOfWork

data class PrisonLeaversPagingDTO(

  var mode: PrisonLeaversJobSort,
  var pageNo: Int,
  var pageSize: Int,
  var typeOfWork: TypeOfWork?,
  var postCode: String?,
  var distance: Long?,
)
