package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationHistoryRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ApplicationDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.HistoriesDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARFilter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture

@Service
class SubjectAccessRequestService(
  private val applicationRepository: ApplicationRepository,
  private val expressionOfInterestRepository: ExpressionOfInterestRepository,
  private val archivedRepository: ArchivedRepository,
  private val applicationHistoryRetriever: ApplicationHistoryRetriever,
) {
  private val atEndOfDay by lazy { LocalTime.MAX.truncatedTo(ChronoUnit.MICROS) }

  @Async
  fun fetchApplications(sarFilter: SARFilter): CompletableFuture<List<ApplicationDTO>> {
    val endTime = sarFilter.toDate?.endAt
    return when {
      endTime != null -> applicationRepository.findByPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(sarFilter.prn, endTime)
      else -> applicationRepository.findByPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }.map { application ->
      val histories = applicationHistoryRetriever.retrieveApplicationHistoriesByDate(application.id.id, sarFilter.fromDate, sarFilter.toDate).map {
        it.entity.run {
          HistoriesDTO(
            firstName = firstName,
            lastName = lastName,
            status = status,
            prisonId = prisonId,
            modifiedAt = lastModifiedAt.toString(),
          )
        }
      }
      val includeOptionalFields = histories.isEmpty()

      ApplicationDTO(
        jobTitle = application.job.title,
        employerName = application.job.employer.name,
        prisonNumber = application.prisonNumber,
        firstName = if (includeOptionalFields) application.firstName else null,
        histories = histories,
        lastName = if (includeOptionalFields) application.lastName else null,
        status = if (includeOptionalFields) application.status else null,
        prisonId = if (includeOptionalFields) application.prisonId else null,
        createdAt = application.createdAt?.toString(),
        lastModifiedAt = application.lastModifiedAt?.toString(),
      )
    }.toList().let { CompletableFuture.completedFuture(it) }
  }

  @Async
  fun fetchExpressionsOfInterest(sarFilter: SARFilter) = sarFilter.toDate?.endAt.let { endTime ->
    when {
      endTime != null -> expressionOfInterestRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(sarFilter.prn, endTime)
      else -> expressionOfInterestRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }.map { it.run { ExpressionOfInterestDTO(job.title, job.employer.name, sarFilter.prn, createdAt.toString()) } }
      .toList().let { CompletableFuture.completedFuture(it) }
  }

  @Async
  fun fetchArchivedJobs(sarFilter: SARFilter) = sarFilter.toDate?.endAt.let { endTime ->
    when {
      endTime != null -> archivedRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(sarFilter.prn, endTime)
      else -> archivedRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }.map { it.run { ArchivedDTO(job.title, job.employer.name, sarFilter.prn, createdAt.toString()) } }
      .toList().let { CompletableFuture.completedFuture(it) }
  }

  private fun LocalDateTime.instant() = this.toInstant(ZoneOffset.UTC)
  private val LocalDate.endAt: Instant get() = this.atTime(atEndOfDay).instant()
}
