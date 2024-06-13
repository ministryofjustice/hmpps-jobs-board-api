package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "job_employers")
class JobEmployer(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "employer_id", nullable = false)
  var id: Long?,

  @Column(name = "employer_name")
  var employerName: String?,

  @Column(name = "employer_bio")
  var employerBio: String?,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: LocalDateTime,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: LocalDateTime,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sector_id", nullable = false)
  var sector: EmployerWorkSector?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "partner_id", nullable = false)
  var partner: EmployerPartner?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "image_id", nullable = false)
  var image: JobImage?,

  @Column(name = "post_code", length = 8)
  var postCode: String?,
)
