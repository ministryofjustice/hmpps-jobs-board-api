package uk.gov.justice.digital.hmpps.jobsboard.data.jsonprofile

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class JobsBoardProfileRequestDTO(

  @Schema(description = "This is the ID of the inmate ", name = "offenderId", pattern = "^[A-Z]\\d{4}[A-Z]{2}\$", required = true)
  val offenderId: String,

  @Schema(description = "This is the prision ID of the inmate ", name = "prisonId")
  var prisonId: String?,

  @Schema(description = "This is the prision Name of the inmate ", name = "prisonName")
  var prisonName: String?,

  @Schema(description = "This is the person who creates the Job.Even though it is passed from front end it wil be automatically set to the right value at the time of record creation ", name = "createdBy", required = false)
  var createdBy: String?,

  @Schema(description = "This is the creation date and time of Job record .Even though it is passed from front end it wil be automatically set to the right value at the time of record creation ", name = "createdDateTime", required = false)
  var createdDateTime: LocalDateTime?,

  @Schema(description = "This is the person who modifies the Job.Even though it is passed from front end it wil be automatically set to the right value at the time of record modification ", name = "modifiedBy", required = false)
  var modifiedBy: String?,

  @Schema(description = "This is the modified date and time of Job record .Even though it is passed from front end it wil be automatically set to the right value at the time of record modification ", name = "modifiedDateTime", required = false)
  var modifiedDateTime: LocalDateTime?,

  @Schema(description = "This is the schema version used", name = "schemaVersion", required = false)
  var schemaVersion: String?,

)
