package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.EntityListeners
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Table(name = "jobs_expressions_of_interest")
@EntityListeners(AuditingEntityListener::class)
data class ExpressionsOfInterest(
  @EmbeddedId
  var id: ExpressionsOfInterestId,

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: Instant? = null,

  @ManyToOne
  @JoinColumn(name = "job_id", referencedColumnName = "id")
  val job: Job,
)
