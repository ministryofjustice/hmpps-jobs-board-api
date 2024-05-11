package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant

@Embeddable
class JobEmployerId(
  @Id
  @Column(name = "employer_id", nullable = false)
  var id: Long?,

  @Column(name = "employer_name", nullable = false)
  var employerName: String?,

  @Column(name = "employer_bio")
  var employerBio: String?,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: Instant?,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: Instant?,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sector_id", nullable = false)
  var sector: EmployerWorkSector?,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "partner_id", nullable = false)
  var partner: EmployerPartner?,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "image_id", nullable = false)
  var image: JobImage?,

  @Column(name = "post_code", length = 8)
  var postCode: String?,
)
