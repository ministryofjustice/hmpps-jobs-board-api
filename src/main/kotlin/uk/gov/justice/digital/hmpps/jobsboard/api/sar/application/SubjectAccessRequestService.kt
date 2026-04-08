package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import org.springframework.data.history.Revision
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationHistoryRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
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
  @Transactional
  fun fetchApplications(sarFilter: SARFilter): CompletableFuture<List<ApplicationDTO>> {
    val endTime = sarFilter.toDate?.endAt
    return when {
      endTime != null -> applicationRepository.findByPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(sarFilter.prn, endTime)
      else -> applicationRepository.findByPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }.map { application ->
      val sortComparator = compareByDescending<Revision<Long, Application>>({ it.revisionNumber.get() }).thenByDescending { it.entity.lastModifiedAt }
      val histories = applicationHistoryRetriever.retrieveAllApplicationHistories(application.id.id).sortedWith(sortComparator).map {
        it.entity.run {
          HistoriesDTO(
            firstName = firstName,
            lastName = lastName,
            status = status,
            additionalInformation = additionalInformation,
            prisonId = prisonId,
            modifiedBy = lastModifiedBy!!,
            modifiedAt = lastModifiedAt.toString(),
          )
        }
      }
      val includeOptionalFields = histories.isEmpty()

      application.run {
        ApplicationDTO(
          jobTitle = job.title,
          employerName = job.employer.name,
          prisonNumber = prisonNumber,
          firstName = firstName.takeIf { includeOptionalFields },
          histories = histories.toList(),
          lastName = lastName.takeIf { includeOptionalFields },
          status = status.takeIf { includeOptionalFields },
          additionalInformation = additionalInformation.takeIf { includeOptionalFields },
          prisonId = prisonId.takeIf { includeOptionalFields },
          createdBy = createdBy!!,
          createdAt = createdAt!!.toString(),
          lastModifiedBy = lastModifiedBy!!,
          lastModifiedAt = lastModifiedAt!!.toString(),
        )
      }
    }.toList().let { CompletableFuture.completedFuture(it) }
  }

  @Async
  @Transactional
  fun fetchExpressionsOfInterest(sarFilter: SARFilter) = sarFilter.toDate?.endAt.let { endTime ->
    when {
      endTime != null -> expressionOfInterestRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(sarFilter.prn, endTime)
      else -> expressionOfInterestRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }.map { it.run { ExpressionOfInterestDTO(job.title, job.employer.name, sarFilter.prn, createdAt.toString()) } }
      .toList().let { CompletableFuture.completedFuture(it) }
  }

  @Async
  @Transactional
  fun fetchArchivedJobs(sarFilter: SARFilter) = sarFilter.toDate?.endAt.let { endTime ->
    when {
      endTime != null -> archivedRepository.findByIdPrisonNumberAndCreatedAtLessThanEqualOrderByCreatedAtDesc(sarFilter.prn, endTime)
      else -> archivedRepository.findByIdPrisonNumberOrderByCreatedAtDesc(sarFilter.prn)
    }.map { it.run { ArchivedDTO(job.title, job.employer.name, sarFilter.prn, createdBy, createdAt.toString()) } }
      .toList().let { CompletableFuture.completedFuture(it) }
  }

  private fun LocalDateTime.instant() = this.toInstant(ZoneOffset.UTC)
  private val LocalDate.endAt: Instant get() = this.atTime(atEndOfDay).instant()
}
