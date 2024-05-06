package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.*

@Entity
@Table(name = "job_source_list")
class JobSourceList (
  @EmbeddedId
  var id: JobSourceListId? ,

  @MapsId("jobSourceId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_source_id", nullable = false)
  var jobSource: JobSource?
)