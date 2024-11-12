package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "jobs_archived")
@EntityListeners(AuditingEntityListener::class)
data class Archived(

  @EmbeddedId
  var id: JobPrisonerId,

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: Instant? = null,

  @ManyToOne
  @MapsId("id")
  @JoinColumn(name = "job_id", referencedColumnName = "id")
  val job: Job,
) {
  override fun toString(): String = "Archived(id=$id, createdAt=$createdAt)"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Archived

    if (id != other.id) return false
    if (createdAt != other.createdAt) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + (createdAt?.hashCode() ?: 0)
    return result
  }
}
