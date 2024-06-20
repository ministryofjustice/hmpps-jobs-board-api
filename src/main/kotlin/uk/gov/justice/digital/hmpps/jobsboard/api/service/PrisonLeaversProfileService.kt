package uk.gov.justice.digital.hmpps.jobsboard.api.service

import jakarta.validation.ValidationException
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
      return PrisonLeaversProfileAndJobsDTO(prisonLeaversProfileRepository.saveAndFlush(prisonLeaversProfile))
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

      return prisonLeaversProfileRepository.saveAndFlush(newPrisonLeaversProfile)?.let { PrisonLeaversProfileAndJobsDTO(it) }!!
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
