package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import java.time.LocalDateTime
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork

open class PrisonLeaversSearchResultDTO(

  var prisonLeaverId: String,
  var jobId: Long,
  var employerName: String,
  var jobTitle: String,
  var typeOfWork: TypeOfWork,
  var closingDate: LocalDateTime,
)
