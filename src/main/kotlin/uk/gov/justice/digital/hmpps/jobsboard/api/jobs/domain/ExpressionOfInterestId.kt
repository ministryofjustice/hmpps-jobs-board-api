package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import java.io.Serializable

const val PRISON_NUMBER_MAX_LENGTH: Int = 7

@Embeddable
data class ExpressionOfInterestId(
  @Column(name = "job_id")
  val jobId: EntityId,

  @Column(name = "prisoner_prison_number")
  val prisonerPrisonNumber: String,
) : Serializable {

  init {
    require(prisonerPrisonNumber.isNotEmpty()) { "prisonerPrisonNumber cannot be empty" }
    require(prisonerPrisonNumber.length <= PRISON_NUMBER_MAX_LENGTH) { "prisonerPrisonNumber is too long" }
  }
}
