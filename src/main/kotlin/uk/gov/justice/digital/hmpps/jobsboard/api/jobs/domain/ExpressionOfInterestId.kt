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

  @Column(name = "prison_number")
  val prisonNumber: String,
) : Serializable {

  init {
    require(prisonNumber.isNotEmpty()) { "prisonNumber cannot be empty" }
    require(prisonNumber.length <= PRISON_NUMBER_MAX_LENGTH) { "prisonNumber is too long" }
  }
}
