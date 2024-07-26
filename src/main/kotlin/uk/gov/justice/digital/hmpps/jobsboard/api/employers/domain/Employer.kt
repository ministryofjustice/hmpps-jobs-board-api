package uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.time.LocalDateTime

@Entity
@Table(name = "employers")
data class Employer(
  @Id
  var id: EntityId,

  @Column(name = "name", nullable = false)
  var name: String,

  @Column(name = "description", length = 500, nullable = false)
  var description: String,

  @Column(name = "sector", nullable = false)
  var sector: String,

  @Column(name = "status", nullable = false)
  val status: String,

  @Column(name = "createdAt", nullable = false)
  var createdAt: LocalDateTime,

  @OneToMany(mappedBy = "employer", cascade = [CascadeType.ALL], orphanRemoval = true)
  val jobs: List<Job> = mutableListOf(),
)
