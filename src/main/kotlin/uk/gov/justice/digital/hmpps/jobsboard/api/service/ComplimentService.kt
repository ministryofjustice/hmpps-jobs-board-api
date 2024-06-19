package uk.gov.justice.digital.hmpps.jobsboard.api.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedJobEmployer
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchResultDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerPartnerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerWorkSectorRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobEmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobImageRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversProfileRepository

@Service
class ComplimentService(
  private val employerWorkSectorRepository: EmployerWorkSectorRepository,
  private val employerPartnerRepository: EmployerPartnerRepository,
  private val jobImageRepository: JobImageRepository,
  private val jobEmployerRepository: JobEmployerRepository,
  private val prisonLeaversProfileRepository: PrisonLeaversProfileRepository,
) {

  fun createJobEmployer(
    jobEmployer: SimplifiedJobEmployer,
  ): SimplifiedJobEmployer {
    jobEmployerRepository.save(jobEmployer)
    return jobEmployer
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
