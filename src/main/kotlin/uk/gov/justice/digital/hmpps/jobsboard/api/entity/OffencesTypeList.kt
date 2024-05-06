package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.*

@Entity
@Table(name = "offences_type_list")
class OffencesTypeList (
  @EmbeddedId
  var id: OffencesTypeListId? ,

  @MapsId("offencesId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "offences_id", nullable = false)
  var offences: OffencesType? ){
}