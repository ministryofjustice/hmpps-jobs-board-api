package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.exceptions.NotFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARContentDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARFilter
import uk.gov.justice.hmpps.kotlin.sar.HmppsPrisonSubjectAccessRequestService
import uk.gov.justice.hmpps.kotlin.sar.HmppsSubjectAccessRequestContent
import java.time.LocalDate

@Service
class PrisonSubjectAccessRequestService(
  private val subjectAccessRequestService: SubjectAccessRequestService,
) : HmppsPrisonSubjectAccessRequestService {

  override fun getPrisonContentFor(
    prn: String,
    fromDate: LocalDate?,
    toDate: LocalDate?,
  ): HmppsSubjectAccessRequestContent? {
    try {
      val sarFilter = SARFilter(prn, fromDate, toDate)
      val listOfJobApplications = subjectAccessRequestService.fetchApplications(sarFilter)
      val listOfExpressionsOfInterest = subjectAccessRequestService.fetchExpressionsOfInterest(sarFilter)
      val listOfArchivedJobs = subjectAccessRequestService.fetchArchivedJobs(sarFilter)

      if (listOfJobApplications.get().isEmpty() && listOfExpressionsOfInterest.get().isEmpty() && listOfArchivedJobs.get().isEmpty()) {
        return null
      }

      return HmppsSubjectAccessRequestContent(
        content = SARContentDTO(
          listOfJobApplications.get(),
          listOfExpressionsOfInterest.get(),
          listOfArchivedJobs.get(),
        ),
      )
    } catch (_: NotFoundException) {
      return null
    }
  }
}
