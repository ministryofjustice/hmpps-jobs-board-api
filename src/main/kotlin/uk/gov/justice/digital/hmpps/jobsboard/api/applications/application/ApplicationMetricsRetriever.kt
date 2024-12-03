package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure.MetricsCountByStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Service
class ApplicationMetricsRetriever(
  private val applicationRepository: ApplicationRepository,
) {
  private val atEndOfDay = LocalTime.MAX.truncatedTo(ChronoUnit.MICROS)

  fun retrieveMetricsSummaryByPrisonIdAndDates(
    prisonId: String,
    dateFrom: LocalDate,
    dateTo: LocalDate,
  ): GetMetricsSummaryResponse {
    return applicationRepository.countApplicantAndJobByPrisonIdAndDateTimeBetween(
      prisonId,
      startTime = dateFrom.startAt,
      endTime = dateTo.endAt,
    )
  }

  fun retrieveMetricsTotalApplicationsPerStageByPrisonIdAndDates(
    prisonId: String,
    dateFrom: LocalDate,
    dateTo: LocalDate,
  ): List<GetMetricsApplicationCountByStatusResponse> {
    return applicationRepository.countApplicationStagesByPrisonIdAndDateTimeBetween(
      prisonId,
      startTime = dateFrom.startAt,
      endTime = dateTo.endAt,
    ).toResponses()
  }

  fun retrieveMetricsLatestApplicationsPerStatusByPrisonIdAndDates(
    prisonId: String,
    dateFrom: LocalDate,
    dateTo: LocalDate,
  ): List<GetMetricsApplicationCountByStatusResponse> {
    return applicationRepository.countApplicationStatusByPrisonIdAndDateTimeBetween(
      prisonId,
      startTime = dateFrom.startAt,
      endTime = dateTo.endAt,
    ).toResponses()
  }

  private val LocalDate.startAt: Instant get() = this.atStartOfDay().instant
  private val LocalDate.endAt: Instant get() = this.atTime(atEndOfDay).instant
  private val LocalDateTime.instant: Instant get() = this.toInstant(ZoneOffset.UTC)

  private fun List<MetricsCountByStatus>.toResponses() =
    this.map { GetMetricsApplicationCountByStatusResponse(it.status, it.count) }
}
