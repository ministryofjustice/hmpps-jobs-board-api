package uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Auditable
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job

@Entity
@Table(name = "employers")
data class Employer(
  @Id
  var id: EntityId,

  @Column(name = "name", nullable = false)
  val name: String,

  @Column(name = "description", length = 1000, nullable = false)
  val description: String,

  @Column(name = "sector", nullable = false)
  val sector: String,

  @Column(name = "status", nullable = false)
  val status: String,

  @OneToMany(mappedBy = "employer", cascade = [CascadeType.ALL], orphanRemoval = true)
  val jobs: List<Job> = mutableListOf(),
) : Auditable() {
  override fun toString(): String = """
    Employer(id=$id,
        name=$name,
        description=$description,
        sector=$sector,
        status=$status,
        createdBy=$createdBy,
        createdAt=$createdAt,
        lastModifiedBy=$lastModifiedBy,
        lastModifiedAt=$lastModifiedAt
    )
  """.trimIndent()
}
