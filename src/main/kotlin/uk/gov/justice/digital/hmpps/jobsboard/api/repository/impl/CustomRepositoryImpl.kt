package uk.gov.justice.digital.hmpps.jobsboard.api.repository.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.repository.query.Param
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchResultDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.CustomRepository

class CustomRepositoryImpl : CustomRepository {
  @PersistenceContext
  private val entityManager: EntityManager? = null

  override fun findMatchingJobsbyClosingDate(@Param("prisonLeaversId") prisonLeaversId: String, @Param("typeOfWorkList") typeOfWorkList: List<String>, @Param("noOfRecords") noOfRecords: Long): MutableList<PrisonLeaversSearchResultDTO>? {
    val query = entityManager?.createNamedQuery("PrisonLeaversProfile.findMatchingJobsClosingSoon", PrisonLeaversSearchResultDTO::class.java)
    query?.setParameter("prisonLeaversId", prisonLeaversId)
    query?.setParameter("typeOfWorkList", typeOfWorkList)
    query?.setParameter("noOfRecords", noOfRecords)
    return query?.getResultList()
  }
}
