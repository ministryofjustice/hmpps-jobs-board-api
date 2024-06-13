package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "job_source")
class JobSource(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "job_source_id", nullable = false)
  var jobSource: Long?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_job_source_name", nullable = false)
  var mnJobSourceName: String?,
)
