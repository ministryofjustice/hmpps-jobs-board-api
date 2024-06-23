package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.SEQUENCE
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.JobEmployerDTO

@Entity
@Table(name = "job_employers")
class JobEmployer(
  @Id @GeneratedValue(strategy = SEQUENCE)
  @Column(name = "employer_id", nullable = false)
  var id: Long?,

  @Column(name = "employer_name")
  var employerName: String?,

  @Column(name = "employer_bio")
  var employerBio: String?,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: LocalDateTime?,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: LocalDateTime?,

  @Column(name = "sector_name", nullable = true)
  var sectorName: String?,

  @Column(name = "grade", nullable = true)
  var grade: String?,

  @Column(name = "partner_name", nullable = true)
  var partnerName: String?,

  @Column(name = "image_path", nullable = true)
  var imagePath: String?,

  @Column(name = "post_code", length = 8)
  var postCode: String?,
){
  constructor(jobEmployerDTO:JobEmployerDTO):this(
    id =jobEmployerDTO.id,
    employerName =jobEmployerDTO.employerName,
    employerBio =jobEmployerDTO.employerBio,
    createdBy =jobEmployerDTO.createdBy,
    createdDateTime =jobEmployerDTO.createdDateTime,
    modifiedBy =jobEmployerDTO.modifiedBy,
    modifiedDateTime =jobEmployerDTO.modifiedDateTime,
    sectorName=jobEmployerDTO.sector,
    grade=jobEmployerDTO.partnerGrade,
    partnerName=jobEmployerDTO.partner,
    imagePath=jobEmployerDTO.image,
    postCode=jobEmployerDTO.postCode
  )
}
