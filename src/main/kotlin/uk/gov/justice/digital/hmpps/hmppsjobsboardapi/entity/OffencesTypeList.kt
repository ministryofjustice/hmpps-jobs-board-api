package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
@Entity
@Table(name = "offences_type_list")
class OffencesTypeList(
  @EmbeddedId
  var id: OffencesTypeListId?,

  @MapsId("offencesId")
  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "offences_id", nullable = false)
  var offences: OffencesType?,
)
