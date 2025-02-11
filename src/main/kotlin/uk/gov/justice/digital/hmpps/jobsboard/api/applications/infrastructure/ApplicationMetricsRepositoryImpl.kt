package uk.gov.justice.digital.hmpps.jobsboard.api.applications.infrastructure

import jakarta.persistence.EntityManager
import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.RevisionType
import org.hibernate.envers.query.AuditEntity
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.GetMetricsSummaryResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import java.time.Instant

@Transactional
class ApplicationMetricsRepositoryImpl(
  val entityManager: EntityManager,
) : ApplicationMetricsRepository {

  override fun countApplicantAndJobByPrisonIdAndDateTimeBetween(
    prisonId: String,
    startTime: Instant,
    endTime: Instant,
  ): GetMetricsSummaryResponse = AuditReaderFactory.get(entityManager).createQuery()
    .forRevisionsOfEntity(Application::class.java, true, true)
    .add(
      AuditEntity.revisionNumber().maximize()
        .add(AuditEntity.property("lastModifiedAt").between(startTime, endTime))
        .add(AuditEntity.property("prisonId").eq(prisonId))
        .add(AuditEntity.revisionType().`in`(arrayOf(RevisionType.ADD, RevisionType.MOD)))
        .computeAggregationInInstanceContext(),
    )
    .addProjection(AuditEntity.property("prisonNumber").countDistinct())
    .addProjection(AuditEntity.property("job_id").countDistinct())
    .singleResult
    .let { it as Array<*> }.let { GetMetricsSummaryResponse(it[0] as Long, it[1] as Long) }
}
