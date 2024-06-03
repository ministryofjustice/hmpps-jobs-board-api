package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "job_type")
data class JobType(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "job_type_id", nullable = false)
  var id: Long?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_job_type_name", nullable = false)
  var mnJobTypeName: String?,
)
