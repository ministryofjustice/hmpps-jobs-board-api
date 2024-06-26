package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import java.time.LocalDateTime

class PLIntrestedJobsClosingSoonDTO(
  var prisonLeaverId: String,
  var jobId: Long,
  var employerName: String,
  var jobTitle: String,
  var closingDate: LocalDateTime,
)
