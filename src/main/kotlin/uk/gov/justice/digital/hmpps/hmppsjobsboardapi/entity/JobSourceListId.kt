package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Embeddable
data class JobSourceListId (
  @Column(name = "job_source_list_id", nullable = false)
  var jobSourceListId: Long? ,

  @Column(name = "job_source_id", nullable = false)
  var jobSourceId: Long? ) : Serializable{

  }