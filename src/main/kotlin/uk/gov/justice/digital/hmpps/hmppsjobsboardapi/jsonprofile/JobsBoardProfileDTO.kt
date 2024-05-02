package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.data.jsonprofile

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobsBoardProfile
import java.time.LocalDateTime

data class JobsBoardProfileDTO(

  @Schema(description = "This is the ID of the inmate ", name = "offenderId", pattern = "^[A-Z]\\d{4}[A-Z]{2}\$", required = true)
  val offenderId: String,

  @Schema(description = "This is the prision ID of the inmate ", name = "prisonId")
  var prisonId: String? = null,

  @Schema(description = "This is the prision Name of the inmate ", name = "prisonName")
  var prisonName: String? = null,
  @Schema(description = "This is the person who creates the Job.Even though it is passed from front end it wil be automatically set to the right value at the time of record creation ", name = "createdBy", required = true)
  var createdBy: String,

  @Schema(description = "This is the creation date and time of Job record .Even though it is passed from front end it wil be automatically set to the right value at the time of record creation ", name = "createdDateTime", required = true)
  var createdDateTime: LocalDateTime,

  @Schema(description = "This is the person who modifies the Job.Even though it is passed from front end it wil be automatically set to the right value at the time of record modification ", name = "modifiedBy", required = true)
  var modifiedBy: String,

  @Schema(description = "This is the schema version used", name = "schemaVersion", required = false)
  var schemaVersion: String? = null,

) {

  constructor(profileEntity: JobsBoardProfile) : this(
    offenderId = profileEntity.offenderId,
    prisonId = profileEntity.prisonId,
    prisonName = profileEntity.prisonName,
    createdBy = profileEntity.createdBy.toString(),
    createdDateTime = profileEntity.createdDateTime!!,
    modifiedBy = profileEntity.modifiedBy!!,
    schemaVersion = profileEntity.schemaVersion,
  )
}
