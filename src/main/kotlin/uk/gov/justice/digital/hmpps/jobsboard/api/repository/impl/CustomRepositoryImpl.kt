package uk.gov.justice.digital.hmpps.jobsboard.api.repository.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.repository.query.Param
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PLIntrestedJobsClosingSoonDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.CustomRepository

class CustomRepositoryImpl : CustomRepository {
  @PersistenceContext
  private val entityManager: EntityManager? = null

  override fun findIntrestedJobsbyClosingDate(@Param("prisonLeaversId") prisonLeaversId: String, ): MutableList<PLIntrestedJobsClosingSoonDTO>? {
    val query = entityManager?.createNamedQuery("PrisonLeaversProfile.findIntrestedJobsClosingSoon", PLIntrestedJobsClosingSoonDTO::class.java)
    query?.setParameter("prisonLeaversId", prisonLeaversId)
    return query?.getResultList()
  }
}
