package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours

@Entity
@Table(name = "hours_type")
class HoursType(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "hours_type_id", nullable = false)
  var id: Long?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_hours_name", nullable = false)
  @Enumerated(EnumType.STRING)
  var mnHoursName: Hours?,
)
