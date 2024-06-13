package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.data

import java.time.Instant

class JobCharity(
  var id: Long?,

  var charityNameName: String?,

  var charityBio: String?,

  var createdBy: String?,

  var createdDateTime: Instant?,
  var modifiedBy: String?,

  var modifiedDateTime: Instant?,

  var image: JobImage?,
)
