package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobsBoardProfile
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository.CustomRepository

class CustomRepositoryImpl : CustomRepository {
  @PersistenceContext
  private val entityManager: EntityManager? = null

  override fun findJobsBoardProfileByEntityGraph(offenderId: String): JobsBoardProfile? {
    val entityGraph = entityManager?.createEntityGraph(JobsBoardProfile::class.java)
    entityGraph?.addAttributeNodes("abilityToWork", "reasonToNotGetWork", "workExperience", "skillsAndInterests", "qualificationsAndTraining", "inPrisonInterests")
    val properties = mutableMapOf<String, Any>()

    properties["jakarta.persistence.fetchgraph"] = entityGraph!!
    return entityManager?.find(JobsBoardProfile::class.java, offenderId, properties)
  }
}
