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

  var sector: EmployerWorkSector?,

  var partner: EmployerPartner?,

  var image: JobImage?,

  var postCode: String?,
)
