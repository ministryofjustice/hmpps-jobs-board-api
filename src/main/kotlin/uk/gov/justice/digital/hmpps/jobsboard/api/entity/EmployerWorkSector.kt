package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "employer_work_sector")
class EmployerWorkSector(
  @Id
  @Column(name = "sector_id", nullable = false)
  var id: Long?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_sector_name", nullable = false)
  var mnSectorName: String?,
)
