package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod

@Entity
@Table(name = "salary_period")
class SalaryPeriod(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "salary_period_id", nullable = false)
  var id: Long?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_salary_period_name", nullable = false)
  @Enumerated(EnumType.STRING)
  var mnSalaryPeriodName: SalaryPeriod?,
)
