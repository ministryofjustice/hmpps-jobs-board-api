package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import java.time.Instant

class JobEmployerDTO(

  var id: Long?,

  var employerName: String?,

  var employerBio: String?,

  var createdBy: String?,

  var createdDateTime: Instant?,

  var modifiedBy: String?,

  var modifiedDateTime: Instant?,

  var sector: EmployerWorkSector?,

  var partner: EmployerPartner?,

  var image: JobImage?,

  var postCode: String?,
)
