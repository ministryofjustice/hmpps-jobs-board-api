package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class JobSourceListId(
  @Column(name = "job_source_list_id", nullable = false)
  var jobSourceListId: Long?,

  @Column(name = "job_source_id", nullable = false)
  var jobSourceId: Long?,
) : Serializable {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as JobSourceListId

    if (jobSourceListId != other.jobSourceListId) return false
    if (jobSourceId != other.jobSourceId) return false

    return true
  }

  override fun hashCode(): Int {
    var result = jobSourceListId?.hashCode() ?: 0
    result = 31 * result + (jobSourceId?.hashCode() ?: 0)
    return result
  }
}
