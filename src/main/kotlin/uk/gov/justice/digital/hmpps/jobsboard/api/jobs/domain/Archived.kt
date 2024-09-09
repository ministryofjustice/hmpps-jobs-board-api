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
  var id: ArchivedId,

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: Instant? = null,

  @ManyToOne
  @MapsId("id")
  @JoinColumn(name = "job_id", referencedColumnName = "id")
  val job: Job,
) {
  override fun toString(): String = "Archived(id=$id, createdAt=$createdAt)"
}
