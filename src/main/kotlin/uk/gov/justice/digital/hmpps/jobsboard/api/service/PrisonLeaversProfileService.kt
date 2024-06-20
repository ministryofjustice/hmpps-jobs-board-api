package uk.gov.justice.digital.hmpps.jobsboard.api.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversProfile
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileAndJobsDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchResultDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobEmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversProfileRepository
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
class PrisonLeaversProfileService(
  private val prisonLeaversProfileRepository: PrisonLeaversProfileRepository,
  private val prisonLeaversJobService: PrisonLeaversJobService,
  private val prisonLeaversJobRepository: PrisonLeaversJobRepository,
  private val jobEmployerRepository: JobEmployerRepository,
) {

  fun createOrUpdatePrisonLeaversProfile(
    prisonLeaversProfileDto: PrisonLeaversProfileDTO,
  ): PrisonLeaversProfileAndJobsDTO {
    var prisonLeaversProfileOptional = prisonLeaversProfileRepository.findById(prisonLeaversProfileDto.offenderId)
    var prisonLeaversProfile: PrisonLeaversProfile? = prisonLeaversProfileOptional.getOrNull()

    if (prisonLeaversProfile != null) {
      var job = prisonLeaversProfileDto.prisonLeaversJob?.id?.let { prisonLeaversJobRepository.findById(prisonLeaversProfileDto.prisonLeaversJob?.id) }
      if (job?.isPresent == true) {
//        prisonLeaversProfile.jobs.add(job.get())
      } else {
        var newJob = prisonLeaversProfileDto.prisonLeaversJob?.let { prisonLeaversJobService.createJob(it) }
        prisonLeaversProfile.jobs.add(newJob)
      }

      prisonLeaversProfile?.modifiedBy = CapturedSpringConfigValues.getDPSPrincipal().displayName
      prisonLeaversProfile?.modifiedDateTime = LocalDateTime.now()
      return PrisonLeaversProfileAndJobsDTO(prisonLeaversProfileRepository.saveAndFlush(prisonLeaversProfile))
    } else {
      var job = prisonLeaversProfileDto.prisonLeaversJob?.id?.let { prisonLeaversJobRepository.findById(prisonLeaversProfileDto.prisonLeaversJob?.id) }
      var newPrisonLeaversProfile = prisonLeaversProfileDto.offenderId?.let {
        PrisonLeaversProfile(
          it,
          "sacintha",
          LocalDateTime.now(),
          "sacintha",
          LocalDateTime.now(),
          mutableListOf(),
        )
      }
      if (job?.isPresent == true) {
        newPrisonLeaversProfile?.jobs?.add(job.get())
      } else {
        var newJob = prisonLeaversProfileDto.prisonLeaversJob?.let { prisonLeaversJobService.createJob(it) }
        newPrisonLeaversProfile?.jobs?.add(newJob)
      }
      return prisonLeaversProfileRepository.save(newPrisonLeaversProfile)?.let { PrisonLeaversProfileAndJobsDTO(it) }!!
    }
  }

  fun searchPrisonLeaversProfile(
    prisonLeaversSearchDTO: PrisonLeaversSearchDTO,
  ): MutableList<PrisonLeaversSearchResultDTO>? {
    var prisonLeaversProfile = prisonLeaversSearchDTO.offenderId?.let {
      prisonLeaversProfileRepository.findMatchingJobsbyClosingDate(
        it,
        prisonLeaversSearchDTO.typeofWorkList.map { it -> it.name },
        prisonLeaversSearchDTO.count,
      )
    }

    return prisonLeaversProfile
  }
}
