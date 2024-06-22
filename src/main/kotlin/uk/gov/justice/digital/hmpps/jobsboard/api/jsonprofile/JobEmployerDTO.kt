package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import java.time.LocalDateTime

class JobEmployerDTO(

  var id: Long?,

  var employerName: String?,

  var employerBio: String?,

  var createdBy: String?,

  var createdDateTime: LocalDateTime?,

  var modifiedBy: String?,

  var modifiedDateTime: LocalDateTime?,

  var sector: String?,

  var partner: String?,
  var partnerGrade: String?,

  var image: String?,

  var postCode: String?,
) {
  constructor(jobEmployer:JobEmployer):this( id =jobEmployer.id,
    employerName =jobEmployer.employerName,
    employerBio =jobEmployer.employerBio,
    createdBy =jobEmployer.createdBy,
    createdDateTime =jobEmployer.createdDateTime,
    modifiedBy =jobEmployer.modifiedBy,
    modifiedDateTime =jobEmployer.modifiedDateTime,
    sector=jobEmployer.sectorName,
    partnerGrade=jobEmployer.grade,
    partner=jobEmployer.partnerName,
    image=jobEmployer.imagePath,
    postCode=jobEmployer.postCode)
}
