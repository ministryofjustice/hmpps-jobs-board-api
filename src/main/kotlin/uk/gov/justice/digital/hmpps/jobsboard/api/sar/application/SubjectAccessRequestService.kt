package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import org.springframework.data.history.Revisions
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationHistoryRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ApplicationDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.HistoriesDTO
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture

@Service
class SubjectAccessRequestService(
  private val applicationRepository: ApplicationRepository,
  private val expressionOfInterestRepository: ExpressionOfInterestRepository,
  private val archivedRepository: ArchivedRepository,
  private val applicationHistoryRetriever: ApplicationHistoryRetriever,
) {

  @Async
  fun fetchApplications(prisonNumber: String?) = applicationRepository.findByPrisonNumber(prisonNumber!!)
    .map {
      it.run {
        ApplicationDTO(
          jobTitle = job.title,
          employerName = job.employer.name,
          prisonNumber = prisonNumber,
          histories = applicationHistoryRetriever.retrieveAllApplicationHistories(prisonNumber, job.id.id)
            ?.applicationHistories().orEmpty(),
          createdAt = createdAt.toString(),
          lastModifiedAt = lastModifiedAt.toString(),
        )
      }
    }.sortedByDescending {
      OffsetDateTime.parse(it.lastModifiedAt.toString())
    }.map {
      it.copy(
        createdAt = formatDateTime(it.createdAt.toString()),
        lastModifiedAt = formatDateTime(it.lastModifiedAt.toString()),
      )
    }.toList().let { CompletableFuture.completedFuture(it) }

  @Async
  fun fetchExpressionsOfInterest(prisonNumber: String) = expressionOfInterestRepository.findByIdPrisonNumber(prisonNumber).map {
    it.run { ExpressionOfInterestDTO(job.title, job.employer.name, prisonNumber, createdAt.toString()) }
  }
    .sortedByDescending {
      OffsetDateTime.parse(it.createdAt.toString())
    }
    .map {
      it.copy(
        createdAt = formatDateTime(it.createdAt.toString()),
      )
    }
    .toList().let { CompletableFuture.completedFuture(it) }

  @Async
  fun fetchArchivedJobs(prisonNumber: String) = archivedRepository.findByIdPrisonNumber(prisonNumber).map {
    it.run { ArchivedDTO(job.title, job.employer.name, prisonNumber, createdAt.toString()) }
  }
    .sortedByDescending {
      OffsetDateTime.parse(it.createdAt.toString())
    }
    .map {
      it.copy(
        createdAt = formatDateTime(it.createdAt.toString()),
      )
    }
    .toList().let { CompletableFuture.completedFuture(it) }

  private fun Revisions<Long, Application>.applicationHistories() = map {
    it.entity.run {
      HistoriesDTO(
        firstName = firstName,
        lastName = lastName,
        status = status,
        prisonName = prisonId,
        modifiedAt = lastModifiedAt.toString(),
      )
    }
  }.sortedByDescending {
    OffsetDateTime.parse(it.modifiedAt)
  }.map {
    val formatted = formatDateTime(it.modifiedAt)
    it.copy(modifiedAt = formatted ?: it.modifiedAt)
  }

  private fun formatDateTime(datetime: String?): String? {
    if (datetime.isNullOrBlank() || datetime == "null") {
      return null
    }

    val parsedDate = OffsetDateTime.parse(datetime)
    return parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  }
}
