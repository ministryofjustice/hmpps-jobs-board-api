package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.io.Serializable

@Embeddable
class OffencesTypeListId(
  @Column(nullable = false)
  var offencesTypeListId: Long?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(nullable = false)
  var offences: OffencesType?,

  @Column(nullable = false)
  var offencesOther: String?,
) : Serializable
