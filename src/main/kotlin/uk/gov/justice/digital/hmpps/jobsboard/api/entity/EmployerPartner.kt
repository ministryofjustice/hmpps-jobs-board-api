package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "employer_partner")
class EmployerPartner(
  @Id
  @Column(name = "partner_id", nullable = false)
  var id: Long?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "partner_grade_id", nullable = false)
  var partnerGrade: EmployerPartnerGrade?,

  @Column(name = "mn_id", nullable = false)
  var mnId: Long?,

  @Column(name = "mn_identifier", nullable = false)
  var mnIdentifier: String?,

  @Column(name = "mn_partner_name", nullable = false)
  var mnPartnerName: String?,
)
