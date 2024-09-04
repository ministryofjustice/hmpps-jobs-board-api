package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import uk.gov.justice.digital.hmpps.jobsboard.api.commons.domain.EntityId
import java.io.Serializable

@Embeddable
data class ExpressionsOfInterestId(
  @Column(name = "job_id")
  var jobId: EntityId,

  @Column(name = "prisoner_prison_number")
  var prisionerPrisonNumber: String,
) : Serializable {

  init {
    requireNotNull(jobId) { "jobId cannot be null: {$jobId}" }
    requireNotNull(prisionerPrisonNumber) { "prisionerPrisonNumber cannot be null: {$prisionerPrisonNumber}" }
  }
}
