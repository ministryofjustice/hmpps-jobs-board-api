package uk.gov.justice.digital.hmpps.jobsboard.api.service

import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversProfile
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversProfileRepository
import java.time.LocalDateTime
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PLIntrestedJobsClosingSoonDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PLIntrestedJobsClosingSoonListDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversCommonSearchDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileAndJobDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileAndJobDetailDTO
import kotlin.jvm.optionals.getOrNull

@Service
class PrisonLeaversProfileService(
  private val prisonLeaversProfileRepository: PrisonLeaversProfileRepository,
  private val prisonLeaversJobRepository: PrisonLeaversJobRepository,
) {

  fun createOrUpdatePrisonLeaversProfile(
    prisonLeaversProfileDto: PrisonLeaversProfileAndJobDTO,
  ): PrisonLeaversProfileAndJobDetailDTO {
    var prisonLeaversProfileOptional = prisonLeaversProfileRepository.findById(prisonLeaversProfileDto.offenderId)
    var prisonLeaversProfile: PrisonLeaversProfile? = prisonLeaversProfileOptional.getOrNull()
    var job = prisonLeaversProfileDto.prisonLeaversJobId?.let { prisonLeaversJobRepository.findById(prisonLeaversProfileDto.prisonLeaversJobId) }

    if (job?.isEmpty == true) {
      throw ValidationException("Employer not for found for job id " + prisonLeaversProfileDto.prisonLeaversJobId)
    }

    if (prisonLeaversProfile != null) {
      if (job?.isPresent == true) {
        if (job != null && !prisonLeaversProfile.jobs.contains(job.get())) {
          prisonLeaversProfile.jobs.add(job.get())
        }
      }

      prisonLeaversProfile?.modifiedBy = CapturedSpringConfigValues.getDPSPrincipal().displayName
      prisonLeaversProfile?.modifiedDateTime = LocalDateTime.now()
      return PrisonLeaversProfileAndJobDetailDTO(prisonLeaversProfileRepository.saveAndFlush(prisonLeaversProfile))
    } else {
      var newPrisonLeaversProfile = prisonLeaversProfileDto.offenderId?.let {
        PrisonLeaversProfile(
          it,
          "sacintha",
          LocalDateTime.now(),
          "sacintha",
          LocalDateTime.now(),
          mutableListOf(job?.get()),
        )
      }

      return prisonLeaversProfileRepository.saveAndFlush(newPrisonLeaversProfile)?.let { PrisonLeaversProfileAndJobDetailDTO(it) }!!
    }
  }

  fun searchPrisonLeaversJobInterest(
    offenderId: String,
  ): MutableList<PLIntrestedJobsClosingSoonDTO>? {
    var prisonLeaversProfile = offenderId?.let {
      prisonLeaversProfileRepository.findIntrestedJobsbyClosingDate(
        it
      )
    }

    return prisonLeaversProfile
  }
}
