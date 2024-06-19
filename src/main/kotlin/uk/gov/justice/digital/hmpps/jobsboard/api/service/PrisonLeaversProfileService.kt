package uk.gov.justice.digital.hmpps.jobsboard.api.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversProfile
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileAndJobsDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchResultDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversProfileRepository
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
class PrisonLeaversProfileService(
  private val prisonLeaversProfileRepository: PrisonLeaversProfileRepository,
  private val prisonLeaversJobRepository: PrisonLeaversJobRepository,
) {

  fun createOrUpdatePrisonLeaversProfile(
    prisonLeaversProfileDto: PrisonLeaversProfileDTO,
  ): PrisonLeaversProfileAndJobsDTO {
    var prisonLeaversProfileOptional = prisonLeaversProfileRepository.findById(prisonLeaversProfileDto.offenderId)
    var prisonLeaversProfile: PrisonLeaversProfile? = prisonLeaversProfileOptional.getOrNull()

    if (prisonLeaversProfile != null) {
      var job = prisonLeaversJobRepository.save(prisonLeaversProfileDto.prisonLeaversJob)
      prisonLeaversProfile.jobs.add(job)
      prisonLeaversProfile?.modifiedBy = CapturedSpringConfigValues.getDPSPrincipal().displayName
      prisonLeaversProfile?.modifiedDateTime = LocalDateTime.now()
    } else {
      var job = prisonLeaversJobRepository.save(prisonLeaversProfileDto.prisonLeaversJob)
      var jobList = mutableListOf(job)
      prisonLeaversProfile = PrisonLeaversProfile(prisonLeaversProfileDto.offenderId!!, CapturedSpringConfigValues.getDPSPrincipal().displayName, LocalDateTime.now(), CapturedSpringConfigValues.getDPSPrincipal().displayName, LocalDateTime.now(), jobList)
    }

    return PrisonLeaversProfileAndJobsDTO(prisonLeaversProfileRepository.save(prisonLeaversProfile))
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
