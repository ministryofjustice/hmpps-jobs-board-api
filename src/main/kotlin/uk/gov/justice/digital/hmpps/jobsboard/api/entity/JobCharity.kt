package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "job_charity")
class JobCharity(
  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "charity_id", nullable = false)
  var id: Long?,

  @Column(name = "charity_name_name")
  var charityNameName: String?,

  @Column(name = "charity_bio")
  var charityBio: String?,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: Instant?,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: Instant?,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "image_id", nullable = false)
  var image: JobImage?,
)