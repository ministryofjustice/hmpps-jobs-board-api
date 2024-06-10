package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.integration.health

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.config.CapturedSpringConfigValues
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.integration.IntegrationTestBase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InfoIntTest : IntegrationTestBase() {

  @Test
  fun `Info page is accessible`() {
    val result = restTemplate.exchange("/info", HttpMethod.GET, HttpEntity<HttpHeaders>(null, null), String::class.java)

    assert(result != null)
    assert(result.hasBody())
    assert(result.statusCode.is2xxSuccessful)
    var stringcompanion = CapturedSpringConfigValues.objectMapper.readTree(result.body?.toString())
    var name = stringcompanion.get("build").get("name")
    Assertions.assertThat(name.asText().toString()).isEqualTo("hmpps-jobs-board-api")
  }

  @Test
  fun `Info page reports version`() {
    val result = restTemplate.exchange("/info", HttpMethod.GET, HttpEntity<HttpHeaders>(null, null), String::class.java)
    assert(result != null)
    assert(result.hasBody())
    assert(result.statusCode.is2xxSuccessful)
    var stringcompanion = CapturedSpringConfigValues.objectMapper.readTree(result.body?.toString())
    var version = stringcompanion.get("build").get("version")
    Assertions.assertThat(
      version?.asText().equals("1_0_0") || version.asText().startsWith(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)),
    )
  }
}
