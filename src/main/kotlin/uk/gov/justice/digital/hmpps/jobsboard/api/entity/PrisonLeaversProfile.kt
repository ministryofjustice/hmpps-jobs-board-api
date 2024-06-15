package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.hateoas.server.core.Relation
import java.time.Instant

@Relation(collectionRelation = "jobs", itemRelation = "job")
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "prison_leavers_profile")
class PrisonLeaversProfile(

  @Id
  @Column(name = "prison_leaver_id", nullable = false)
  var id: String,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: Instant?,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: Instant?,

  @ManyToMany
  @JoinTable(
    name = "Prison_Leaver_Job_Table",
    joinColumns = arrayOf(JoinColumn(name = "prison_leaver_id")),
    inverseJoinColumns = arrayOf(JoinColumn(name = "prisonLeaversJob_id")),
  )
  var jobs: Set<PrisonLeaversJob>,
)
