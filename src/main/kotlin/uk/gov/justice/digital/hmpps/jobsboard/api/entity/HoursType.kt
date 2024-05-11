package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "hours_type")
class HoursType(
  @Id
  @Column(name = "hours_type_id", nullable = false)
  var id: Long?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_hours_name", nullable = false)
  var mnHoursName: String?,
)
