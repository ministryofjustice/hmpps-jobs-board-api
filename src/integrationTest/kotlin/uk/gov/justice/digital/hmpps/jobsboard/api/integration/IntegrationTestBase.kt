package uk.gov.justice.digital.hmpps.jobsboard.api.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.jobsboard.api.HmppsJobsBoardApi
import uk.gov.justice.digital.hmpps.jobsboard.api.integration.helpers.JwtAuthHelper

@SpringBootTest(
  webEnvironment = RANDOM_PORT,
  classes = arrayOf(
    HmppsJobsBoardApi::class,
  ),
)
@ActiveProfiles("integration-test")
abstract class IntegrationTestBase internal constructor() {

  companion object {

  /*  private val postgresContainer = PostgresContainer.instance

    @JvmStatic
    @DynamicPropertySource
    fun configureTestContainers(registry: DynamicPropertyRegistry) {
      postgresContainer?.run {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
        registry.add("spring.datasource.username", postgresContainer::getUsername)
        registry.add("spring.datasource.password", postgresContainer::getPassword)
        registry.add("spring.datasource.placeholders.database_update_password", postgresContainer::getPassword)
        registry.add("spring.datasource.placeholders.database_read_only_password", postgresContainer::getPassword)
        registry.add("spring.flyway.url", postgresContainer::getJdbcUrl)
        registry.add("spring.flyway.user", postgresContainer::getUsername)
        registry.add("spring.flyway.password", postgresContainer::getPassword)
      }
    }*/
  }

  @Autowired
  lateinit var restTemplate: TestRestTemplate

  @Autowired
  lateinit var jwtAuthHelper: JwtAuthHelper
  internal fun setAuthorisation(
    user: String = "test-client",
    roles: List<String> = listOf(),
  ): (HttpHeaders) {
    return jwtAuthHelper.setAuthorisationForUnitTests(user, roles)
  }
}
