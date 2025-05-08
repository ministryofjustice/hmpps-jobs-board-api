package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.data.history.Revisions
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class ApplicationHistoryRetriever(
  private val applicationRepository: ApplicationRepository,
) {
  private val atEndOfDay by lazy { LocalTime.MAX.truncatedTo(ChronoUnit.MICROS) }

  fun retrieveAllApplicationHistories(prisonNumber: String, jobId: String): Revisions<Long, Application>? = retrieveLatestApplication(prisonNumber, jobId)?.let { retrieveAllApplicationHistories(it.id.id) }

  fun retrieveAllApplicationHistories(applicationId: String) = retrieveAllApplicationHistories(EntityId(applicationId))

  private fun retrieveAllApplicationHistories(applicationId: EntityId) = applicationRepository.findRevisions(applicationId)

  private fun retrieveLatestApplication(prisonNumber: String, jobId: String) = applicationRepository.findTopByPrisonNumberAndJobIdIdOrderByCreatedAtDesc(prisonNumber, jobId)
}
