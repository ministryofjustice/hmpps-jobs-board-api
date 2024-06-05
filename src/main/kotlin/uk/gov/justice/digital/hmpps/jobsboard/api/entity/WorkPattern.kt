package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.ContractHours

@Entity
@Table(name = "work_pattern")
data class WorkPattern(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "work_pattern_id", nullable = false)
  var id: Long?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_work_pattern_name", nullable = false)
  @Enumerated(EnumType.STRING)
  var mnWorkPatternName: ContractHours?,
)
