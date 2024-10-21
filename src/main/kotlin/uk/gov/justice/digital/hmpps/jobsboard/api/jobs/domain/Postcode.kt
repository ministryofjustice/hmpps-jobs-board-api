package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Entity
@Table(name = "postcodes")
data class Postcode(
  @Id
  var id: EntityId,

  @Column(name = "code", length = 7, nullable = false)
  var code: String,

  @Column(name = "x_coordinate", nullable = false)
  var xCoordinate: Double,

  @Column(name = "y_coordinate", nullable = false)
  var yCoordinate: Double,
) : Auditable() {
  override fun toString(): String = """
    Postcode(
      id=$id,
      code=$code,
      xCoordinate=$xCoordinate,
      yCoordinate=$yCoordinate,
      createdBy=$createdBy,
      createdAt=$createdAt,
      lastModifiedBy=$lastModifiedBy,
      lastModifiedAt=$lastModifiedAt
    )
  """.trimIndent()
}
