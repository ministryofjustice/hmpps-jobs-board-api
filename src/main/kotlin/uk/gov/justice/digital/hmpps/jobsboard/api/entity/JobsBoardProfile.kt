package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import uk.gov.justice.digital.hmpps.jobsboard.api.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.jobsboard.data.jsonprofile.JobsBoardProfileRequestDTO
import java.time.LocalDateTime

@Entity
@Table(name = "CM_PROFILE")
data class JobsBoardProfile(
  @Id
  val offenderId: String,
  @Column(name = "PRISON_ID")
  var prisonId: String?,

  @Column(name = "PRISON_NAME")
  var prisonName: String?,
  @CreatedBy
  var createdBy: String?,

  @CreatedDate
  var createdDateTime: LocalDateTime?,

  @LastModifiedBy
  var modifiedBy: String?,
  @Column(name = "SCHEMA_VERSION")
  var schemaVersion: String?,
  @LastModifiedDate
  var modifiedDateTime: LocalDateTime?,

) {

  constructor(
    jobsBoardProfileRequestDTO: JobsBoardProfileRequestDTO,
  ) : this(
    offenderId = jobsBoardProfileRequestDTO.offenderId!!,
    createdBy = jobsBoardProfileRequestDTO.createdBy,
    createdDateTime = jobsBoardProfileRequestDTO.createdDateTime,
    modifiedBy = jobsBoardProfileRequestDTO.modifiedBy,
    prisonId = jobsBoardProfileRequestDTO.prisonId,
    prisonName = jobsBoardProfileRequestDTO.prisonName,
    schemaVersion = jobsBoardProfileRequestDTO.schemaVersion,
    modifiedDateTime = jobsBoardProfileRequestDTO.modifiedDateTime,
  )

  @PrePersist
  fun prePersist() {
    this.createdBy = CapturedSpringConfigValues.getDPSPrincipal().name
    this.createdDateTime = LocalDateTime.now()
    this.modifiedBy = CapturedSpringConfigValues.getDPSPrincipal().name
  }

  @PreUpdate
  fun preUpdate() {
    this.modifiedBy = CapturedSpringConfigValues.getDPSPrincipal().name
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as JobsBoardProfile

    if (offenderId != other.offenderId) return false
    if (prisonId != other.prisonId) return false
    if (prisonName != other.prisonName) return false
    if (createdBy != other.createdBy) return false
    if (createdDateTime != other.createdDateTime) return false
    if (modifiedBy != other.modifiedBy) return false
    if (schemaVersion != other.schemaVersion) return false
    if (modifiedDateTime != other.modifiedDateTime) return false

    return true
  }

  override fun hashCode(): Int {
    var result = offenderId.hashCode()
    result = 31 * result + (prisonId?.hashCode() ?: 0)
    result = 31 * result + (prisonName?.hashCode() ?: 0)
    result = 31 * result + (createdBy?.hashCode() ?: 0)
    result = 31 * result + (createdDateTime?.hashCode() ?: 0)
    result = 31 * result + (modifiedBy?.hashCode() ?: 0)
    result = 31 * result + (schemaVersion?.hashCode() ?: 0)
    result = 31 * result + (modifiedDateTime?.hashCode() ?: 0)
    return result
  }

  override fun toString(): String {
    return "JobsBoardProfile(offenderId='$offenderId', prisonId=$prisonId, prisonName=$prisonName, createdBy=$createdBy, createdDateTime=$createdDateTime, modifiedBy=$modifiedBy, schemaVersion=$schemaVersion, modifiedDateTime=$modifiedDateTime)"
  }
}
