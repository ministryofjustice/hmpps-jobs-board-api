package uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.data

import java.io.Serializable
import java.util.*

data class JobSourceListId(
  var jobSourceListId: Long?,

  var jobSourceId: Long?,
) : Serializable
