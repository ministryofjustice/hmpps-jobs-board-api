package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.GetMetricsSummaryResponse
import java.time.Instant

interface ApplicationMetricsRepository {
  fun countApplicantAndJobByPrisonIdAndDateTimeBetween(
    prisonId: String,
    startTime: Instant,
    endTime: Instant,
  ): GetMetricsSummaryResponse
}
