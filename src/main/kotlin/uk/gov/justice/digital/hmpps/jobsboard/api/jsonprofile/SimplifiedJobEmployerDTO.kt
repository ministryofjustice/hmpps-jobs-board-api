package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import java.time.LocalDateTime

class SimplifiedJobEmployerDTO(

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
)
