package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "job_source_list")
class JobSourceList(
  @Id
  var id: Long?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_source_id", nullable = false)
  var jobSource: JobSource?,
)
