package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "employers")
data class Employer(
  @Id
  @Column(name = "id", nullable = false)
  var id: EntityId,

  @Column(name = "name", nullable = false)
  var name: String,

  @Column(name = "description", length = 500, nullable = false)
  var description: String,

  @Column(name = "created_by", nullable = true)
  var createdBy: String?,

  @Column(name = "created_when", nullable = true)
  var createdWhen: LocalDateTime?,

  @Column(name = "modified_by", nullable = true)
  var modifiedBy: String?,

  @Column(name = "modified_when", nullable = true)
  var modifiedWhen: LocalDateTime?,

  @Column(name = "sector", nullable = false)
  var sector: String,

  @Column(name = "status", nullable = false)
  val status: String,
)
