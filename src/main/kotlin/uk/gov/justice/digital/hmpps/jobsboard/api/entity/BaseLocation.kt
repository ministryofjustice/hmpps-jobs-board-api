package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "base_location")
class BaseLocation {
  @Id
  @Column(name = "base_location_id", nullable = false)
  var id: Long? = null

  @Column(name = "mn_id", nullable = false)
  var mnId: Long? = null

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String? = null

  @Column(name = "mn_sector_name", nullable = false)
  var mnSectorName: String? = null
}
