package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "jobs_expressions_of_interest")
@EntityListeners(AuditingEntityListener::class)
data class ExpressionOfInterest(

  @EmbeddedId
  var id: JobPrisonerId,

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: Instant? = null,

  @MapsId("id")
  @JoinColumn(name = "job_id", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.EAGER)
  val job: Job,
) {
  override fun toString(): String = "ExpressionOfInterest(id=$id, createdAt=$createdAt)"
}
