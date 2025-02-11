package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus

@Service
class ApplicationByPrisonerRetriever(
  private val applicationRepository: ApplicationRepository,
) {
  private val openStatus = ApplicationStatus.openStatus.map { it.name }
  private val closedStatus = ApplicationStatus.closedStatus.map { it.name }

  fun retrieveAllOpenApplications(prisonNumber: String, pageable: Pageable): Page<Application> = applicationRepository.findByPrisonNumberAndStatusIn(prisonNumber, openStatus, pageable)

  fun retrieveAllClosedApplications(prisonNumber: String, pageable: Pageable): Page<Application> = applicationRepository.findByPrisonNumberAndStatusIn(prisonNumber, closedStatus, pageable)
}
