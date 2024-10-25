package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository

@Service
class ApplicationRetriever(
  private val applicationRepository: ApplicationRepository,
) {
  fun retrieveAllApplicationsByPrisonId(
    prisonId: String,
    prisonerName: String? = null,
    status: List<String>? = null,
    jobTitleOrEmployerName: String? = null,
    pageable: Pageable,
  ): Page<Application> {
    return when {
      prisonerName.isNullOrEmpty() && status.isNullOrEmpty() && jobTitleOrEmployerName.isNullOrEmpty() ->
        applicationRepository.findByPrisonId(prisonId, pageable)

      else -> applicationRepository.findByPrisonIdAndPrisonerNameAndApplicationStatusAndJobTitleOrEmployerName(
        prisonId = prisonId,
        prisonerName = prisonerName,
        status = status,
        jobTitleOrEmployerName = jobTitleOrEmployerName,
        pageable = pageable,
      )
    }
  }
}
