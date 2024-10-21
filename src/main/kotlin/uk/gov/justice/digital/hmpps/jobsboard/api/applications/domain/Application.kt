package uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import uk.gov.justice.digital.hmpps.jobsboard.api.audit.domain.AuditedEntity
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

@Entity
@Audited
@EntityListeners(AuditingEntityListener::class)
@Table(name = "applications")
data class Application(
  @Id
  var id: EntityId,

  @Column(name = "prison_number", nullable = false)
  val prisonNumber: String,

  @Column(name = "prison_id", nullable = false)
  val prisonId: String,

  @Column(name = "first_name")
  val firstName: String? = null,

  @Column(name = "last_name")
  val lastName: String? = null,

  @Column(name = "status", nullable = false)
  val status: String,

  @Column(name = "additional_information")
  val additionalInformation: String? = null,

  @JoinColumn(name = "job_id", referencedColumnName = "id")
  @ManyToOne
  @Audited(targetAuditMode = NOT_AUDITED)
  val job: Job,
) : AuditedEntity() {
  override fun toString() = """
    Application(
      id=$id,
      jobId=${job.id}, 
      prisonNumber=$prisonNumber, 
      prisonId=$prisonId, 
      firstName=$firstName, 
      lastName=$lastName, 
      status=$status, 
      additionalInformation=$additionalInformation,
      createdBy=$createdBy, 
      createdAt=$createdAt,
      lastModifiedBy=$lastModifiedBy, 
      lastModifiedAt=$lastModifiedAt
    )
  """.trimIndent()
}
