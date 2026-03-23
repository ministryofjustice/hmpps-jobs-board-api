package uk.gov.justice.digital.hmpps.jobsboard.api.sar

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.ApplicationTestCase
import uk.gov.justice.digital.hmpps.subjectaccessrequest.SarFlywaySchemaTest
import uk.gov.justice.digital.hmpps.subjectaccessrequest.SarIntegrationTestHelper
import uk.gov.justice.digital.hmpps.subjectaccessrequest.SarJpaEntitiesTest
import javax.sql.DataSource

/**
 * This SAR test checks Flyway schema and JPA Entity (detect schema/entity changes)
 */
class SubjectAccessRequestDatabaseTest :
  ApplicationTestCase(),
  SarFlywaySchemaTest,
  SarJpaEntitiesTest {

  @Autowired
  lateinit var dataSource: DataSource

  @Autowired
  lateinit var entityManager: EntityManager

  override fun getSarHelper(): SarIntegrationTestHelper = sarIntegrationTestHelper
  override fun getDataSourceInstance(): DataSource = dataSource
  override fun getEntityManagerInstance(): EntityManager = entityManager
}
