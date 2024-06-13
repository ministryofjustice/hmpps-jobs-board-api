package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "job_source_list")
class JobSourceList(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  var id: Long?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_source_id", nullable = false)
  var jobSource: JobSource?,
)
