package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
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

  private val LocalDateTime.instant: Instant get() = this.toInstant(ZoneOffset.UTC)
}
