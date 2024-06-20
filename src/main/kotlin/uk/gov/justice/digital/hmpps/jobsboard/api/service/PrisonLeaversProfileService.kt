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
      var job = prisonLeaversProfileDto.prisonLeaversJob?.let { prisonLeaversJobService.createJob(it) }
      prisonLeaversProfile.jobs.add(job)
      prisonLeaversProfile?.modifiedBy = CapturedSpringConfigValues.getDPSPrincipal().displayName
      prisonLeaversProfile?.modifiedDateTime = LocalDateTime.now()
      return PrisonLeaversProfileAndJobsDTO(prisonLeaversProfileRepository.saveAndFlush(prisonLeaversProfile))
    } else {
//      var job = prisonLeaversProfileDto.prisonLeaversJob?.let { prisonLeaversJobService.createJob(it) }
      val employer = prisonLeaversProfileDto.prisonLeaversJob?.employer?.let { jobEmployerRepository.save(it) }
      prisonLeaversProfileDto.prisonLeaversJob?.employer = employer
      prisonLeaversJobRepository.save(prisonLeaversProfileDto.prisonLeaversJob)
      var jobList = mutableListOf(prisonLeaversProfileDto.prisonLeaversJob)
      prisonLeaversProfile = PrisonLeaversProfile(prisonLeaversProfileDto.offenderId!!, CapturedSpringConfigValues.getDPSPrincipal().displayName, LocalDateTime.now(), CapturedSpringConfigValues.getDPSPrincipal().displayName, LocalDateTime.now(), jobList)
      return PrisonLeaversProfileAndJobsDTO(prisonLeaversProfileRepository.saveAndFlush(prisonLeaversProfile))
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
