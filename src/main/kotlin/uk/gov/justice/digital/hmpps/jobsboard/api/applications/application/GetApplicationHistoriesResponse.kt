package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.data.history.Revision
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application

data class GetApplicationHistoriesResponse(
  val id: String,
  val jobId: String,
  val prisonNumber: String,
  val prisonId: String,
  val firstName: String?,
  val lastName: String?,
  val applicationStatus: String,
  val additionalInformation: String?,
  val createdBy: String,
  val createdAt: String,
  val modifiedBy: String,
  val modifiedAt: String,
) {
  companion object {
    fun from(revision: Revision<Long, Application>) = revision.entity.run {
      GetApplicationHistoriesResponse(
        id = id.id,
        jobId = job.id.id,
        prisonNumber = prisonNumber,
        prisonId = prisonId,
        firstName = firstName,
        lastName = lastName,
        applicationStatus = status,
        additionalInformation = additionalInformation,
        createdBy = createdBy ?: "",
        createdAt = createdAt.toString(),
        modifiedBy = lastModifiedBy ?: "",
        modifiedAt = lastModifiedAt.toString(),
      )
    }
  }
}
