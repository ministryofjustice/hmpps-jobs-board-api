package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import org.springframework.data.history.Revisions
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationHistoryRetriever
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Archived
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ArchivedRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.ExpressionOfInterestRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ApplicationDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.HistoriesDTO
import java.util.concurrent.CompletableFuture

@Service
class SubjectAccessRequestService(
  private val applicationRepository: ApplicationRepository,
  private val expressionOfInterestRepository: ExpressionOfInterestRepository,
  private val archivedRepository: ArchivedRepository,
  private val applicationHistoryRetriever: ApplicationHistoryRetriever,
) {

  @Async
  fun fetchApplications(prisonNumber: String?): CompletableFuture<List<ApplicationDTO>> {
    val applications: List<Application> = applicationRepository.findByPrisonNumber(prisonNumber!!)
    val applicationsDTO = applications
      .map { jobApplication: Application ->
        val applicationHistory = getApplicationHistories(applicationHistoryRetriever.retrieveAllApplicationHistories(prisonNumber, jobApplication.job.id.id))
        ApplicationDTO(
          jobApplication.job.title,
          jobApplication.job.employer.name,
          jobApplication.prisonId,
          applicationHistory,
          jobApplication.createdBy,
          jobApplication.lastModifiedBy,
        )
      }.toList()
    return CompletableFuture.completedFuture(applicationsDTO)
  }

  fun getApplicationHistories(revisions: Revisions<Long, Application>?): List<HistoriesDTO> {
    val applicationHistory: List<HistoriesDTO> = revisions?.map { revision ->
      val application: Application = revision.entity
      HistoriesDTO(
        firstName = application.firstName ?: "Unknown",
        lastName = application.lastName ?: "Unknown",
        status = application.status,
        prisonName = application.prisonId,
        modifiedAt = application.lastModifiedAt.toString(),
      )
    }?.toList().orEmpty()

    return applicationHistory
  }

  @Async
  fun fetchExpressionsOfInterest(prisonNumber: String): CompletableFuture<List<ExpressionOfInterestDTO>> {
    val eoiList: List<ExpressionOfInterest> = expressionOfInterestRepository.findByIdPrisonNumber(prisonNumber)
    val eoiDTOs: List<ExpressionOfInterestDTO> = eoiList.stream()
      .map { eoi: ExpressionOfInterest ->
        ExpressionOfInterestDTO(
          eoi.job.title,
          eoi.job.employer.name,
          prisonNumber,
          eoi.createdAt.toString(),
        )
      }.toList()
    return CompletableFuture.completedFuture(eoiDTOs)
  }

  @Async
  fun fetchArchivedJobs(prisonNumber: String): CompletableFuture<List<ArchivedDTO>> {
    val archivedJobs: List<Archived> = archivedRepository.findByIdPrisonNumber(prisonNumber)
    val archivedJobDTO: List<ArchivedDTO> = archivedJobs
      .map { archivedJob: Archived ->
        ArchivedDTO(
          archivedJob.job.title,
          archivedJob.job.employer.name,
          prisonNumber,
          archivedJob.createdAt.toString(),
        )
      }.toList()
    return CompletableFuture.completedFuture(archivedJobDTO)
  }
}
