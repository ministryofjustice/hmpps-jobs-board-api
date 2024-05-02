package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.HmppsJobsBoardApi
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.integration.helpers.JwtAuthHelper

@SpringBootTest(
  webEnvironment = RANDOM_PORT,
  classes = arrayOf(
    HmppsJobsBoardApi::class,
  ),
)
@ActiveProfiles("test")
abstract class IntegrationTestBase internal constructor() {

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
