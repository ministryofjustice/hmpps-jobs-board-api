package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import java.time.LocalDateTime

open class PrisonLeaversSearchResultDTO(

  var prisonLeaverId: String,
  var jobId: Long,
  var employerName: String,
  var jobTitle: String,
  var typeOfWork: TypeOfWork,
  var closingDate: LocalDateTime,
)
