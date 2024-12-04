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
    val startTime = dateFrom.atStartOfDay().instant
    val endTime = dateTo.atTime(atEndOfDay).instant

    return applicationRepository.countApplicantAndJobByPrisonIdAndDateTimeBetween(prisonId, startTime, endTime)
  }

  fun retrieveMetricsTotalApplicationsPerStageByPrisonIdAndDates(
    prisonId: String,
    dateFrom: LocalDate,
    dateTo: LocalDate,
  ): List<GetMetricsApplicationCountByStatusResponse> {
    val startTime = dateFrom.atStartOfDay().instant
    val endTime = dateTo.atTime(atEndOfDay).instant

    return applicationRepository.countApplicationStagesByPrisonIdAndDateTimeBetween(prisonId, startTime, endTime)
      .toResponses()
  }

  private val LocalDateTime.instant: Instant get() = this.toInstant(ZoneOffset.UTC)

  private fun List<MetricsCountByStatus>.toResponses() =
    this.map { GetMetricsApplicationCountByStatusResponse(it.status, it.count) }
}
