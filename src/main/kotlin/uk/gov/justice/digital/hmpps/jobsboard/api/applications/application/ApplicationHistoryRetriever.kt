package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.data.history.Revisions
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Service
class ApplicationHistoryRetriever(
  private val applicationRepository: ApplicationRepository,
) {
  fun retrieveAllApplicationHistories(prisonNumber: String, jobId: String): Revisions<Long, Application>? {
    val application = applicationRepository.findByPrisonNumberAndJobIdId(prisonNumber, jobId)
      .sortedByDescending { it.createdAt }.firstOrNull()

    return application?.let { retrieveAllApplicationHistories(application.id.id) }
  }

  fun retrieveAllApplicationHistories(applicationId: String) =
    retrieveAllApplicationHistories(EntityId(applicationId))

  private fun retrieveAllApplicationHistories(applicationId: EntityId) =
    applicationRepository.findRevisions(applicationId)
}
