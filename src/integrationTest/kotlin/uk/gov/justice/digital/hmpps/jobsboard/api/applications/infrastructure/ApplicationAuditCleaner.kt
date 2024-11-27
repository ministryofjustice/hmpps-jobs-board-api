package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ApplicationAuditCleaner(val entityManager: EntityManager) {
  @Transactional
  fun deleteAllRevisions() {
    entityManager.createNativeQuery("delete from applications_audit").executeUpdate()
  }
}
