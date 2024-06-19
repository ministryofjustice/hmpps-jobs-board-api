package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversProfile
import java.time.LocalDateTime

class PrisonLeaversProfileAndJobsDTO(

  var id: String,

  var createdBy: String?,

  var createdDateTime: LocalDateTime?,

  var modifiedBy: String?,

  var modifiedDateTime: LocalDateTime?,

  var jobs: MutableList<PrisonLeaversJob?>,
) {
  constructor(prisonLeaversProfile: PrisonLeaversProfile) : this(
    id = prisonLeaversProfile.id,
    createdBy = prisonLeaversProfile.createdBy,
    createdDateTime = prisonLeaversProfile.createdDateTime,
    modifiedBy = prisonLeaversProfile.modifiedBy,
    jobs = prisonLeaversProfile.jobs.toMutableList(),
    modifiedDateTime = prisonLeaversProfile.modifiedDateTime,
   )
}
