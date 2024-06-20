package uk.gov.justice.digital.hmpps.jobsboard.api.service

import jakarta.validation.ValidationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.assemblers.EmployerJobModelAssembler
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedPrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.PrisonLeaversJobSort
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobDetailDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobListPageDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversPagingDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.SimplifiedPrisonLeaversJobDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobEmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.telemetry.TelemetryService

@Service
class PrisonLeaversJobService(
  private val prisonLeaversRepository: PrisonLeaversJobRepository,
  private val jobEmployerRepository: JobEmployerRepository,
  private val outboundEventsService: OutboundEventsService,
  private val telemetryService: TelemetryService,
  private val employerJobModelAssembler: EmployerJobModelAssembler,
) {

  fun getPagingList(requestDTO: PrisonLeaversPagingDTO): PrisonLeaversJobListPageDTO {
    var sortByParam = Sort.unsorted()
    val postCode = requestDTO.postCode
    val typeOfWork = requestDTO.typeOfWork
    when (requestDTO.sort) {
      PrisonLeaversJobSort.ALL -> sortByParam = Sort.by("typeOfWork").and(Sort.by("postCode"))
      PrisonLeaversJobSort.LOCATION_AND_TYPE_OF_WORK -> sortByParam = Sort.by("typeOfWork").and(Sort.by("postCode"))
      PrisonLeaversJobSort.TYPE_OF_WORK -> sortByParam = Sort.by("typeOfWork")
      else -> {
        sortByParam = Sort.unsorted()
      }
    }

    val paging: Pageable = PageRequest.of(
      requestDTO.pageNo.toInt(),
      requestDTO.pageSize.toInt(),
      sortByParam,
    )

    val pagedResult: Page<SimplifiedPrisonLeaversJob>? =
      when {
        typeOfWork != null && !postCode.isNullOrEmpty() -> prisonLeaversRepository.findPrisonLeaversJobsByTypeOfWorkAndEmployerPostCode(typeOfWork, postCode, paging)
        typeOfWork != null -> prisonLeaversRepository.findPrisonLeaversJobsByTypeOfWork(typeOfWork, paging)
        !postCode.isNullOrEmpty() -> prisonLeaversRepository.findPrisonLeaversJobsByEmployerPostCode(postCode, paging)
        else -> prisonLeaversRepository.findAll(paging)
      }

    val pagedModel: PrisonLeaversJobListPageDTO? = pagedResult?.let { employerJobModelAssembler.toCollectionModelList(it) }
    return pagedModel!!
  }

  fun createJob(
    prisonLeaversJob: SimplifiedPrisonLeaversJobDTO,
  ): SimplifiedPrisonLeaversJob {
    val employer = jobEmployerRepository.findById(prisonLeaversJob.employerId)
    if (employer.isPresent == true) {
      var simplifiedPrisonLeaversJob = SimplifiedPrisonLeaversJob(prisonLeaversJob, employer.get())
      return prisonLeaversRepository.saveAndFlush(simplifiedPrisonLeaversJob)
    }
    throw ValidationException("Employer not for found ofr employerId " + prisonLeaversJob.employerId)
  }

  fun getPrisonersLeaversJob(prisonerLeaversJobId: Long): PrisonLeaversJobDetailDTO {
    return PrisonLeaversJobDetailDTO(prisonLeaversRepository.findById(prisonerLeaversJobId).get())
  }
}
