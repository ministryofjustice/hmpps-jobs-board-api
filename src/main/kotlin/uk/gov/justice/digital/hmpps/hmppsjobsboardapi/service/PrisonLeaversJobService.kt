package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.PrisonLeaversJobSort
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.messaging.OutboundEventsService
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.telemetry.TelemetryService

@Service
class PrisonLeaversJobService(
  private val prisonLeaversRepository: PrisonLeaversJobRepository,
  private val outboundEventsService: OutboundEventsService,
  private val telemetryService: TelemetryService,
) {

  fun getPagingList(pageNo: Int, pageSize: Int, sortBy: PrisonLeaversJobSort): MutableList<PrisonLeaversJob>? {
    var sortByParam = Sort.unsorted()
    if (sortBy.equals(PrisonLeaversJobSort.ALL)) {
      when (sortBy) {
        PrisonLeaversJobSort.ALL -> sortByParam = Sort.by("typeOfWork").and(Sort.by("postCode"))
        PrisonLeaversJobSort.LOCATION_AND_TYPE_OF_WORK -> sortByParam = Sort.by("typeOfWork").and(Sort.by("postCode"))
        PrisonLeaversJobSort.TYPE_OF_WORK -> sortByParam = Sort.by("typeOfWork")
        else -> {
          sortByParam = Sort.unsorted()
        }
      }
    }
    val paging: Pageable = PageRequest.of(
      pageNo.toInt(),
      pageSize.toInt(),
      sortByParam,
    )

    val pagedResult: Page<PrisonLeaversJob> = prisonLeaversRepository.findAll(paging)
    if (pagedResult.hasContent()) {
      return pagedResult.getContent()
    } else {
      return ArrayList<PrisonLeaversJob>()
    }
  }

  fun createJob(
    prisonLeaversJob: PrisonLeaversJob,
  ): PrisonLeaversJob {
    /*  var jobEmployer: JobEmployer = prisonLeaversRepository.save(

    )*/
    /*jobsProfileDTO.id = jobEmployer.id
    return jobsProfileDTO*/
    return prisonLeaversRepository.save(prisonLeaversJob)
  }

  fun getPrisonersLeaversJob(prisonerLeaversJobId: Long): PrisonLeaversJob {
    return prisonLeaversRepository.getReferenceById(prisonerLeaversJobId)
  }
}
