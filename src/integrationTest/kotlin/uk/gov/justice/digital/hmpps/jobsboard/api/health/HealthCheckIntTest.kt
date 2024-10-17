package uk.gov.justice.digital.hmpps.jobsboard.api.health

import uk.gov.justice.digital.hmpps.jobsboard.api.commons.application.ApplicationTestCase

class HealthCheckIntTest : ApplicationTestCase() {

//  @Test
//  fun `Health page reports ok`() {
//    val result = restTemplate.exchange("/health", HttpMethod.GET, HttpEntity<HttpHeaders>(null, null), String::class.java)
//    assert(result != null)
//    assert(result.hasBody())
//    assert(result.statusCode.is2xxSuccessful)
//  }

//  @Test
//  fun `Health info reports version`() {
//    val result = restTemplate.exchange("/health", HttpMethod.GET, HttpEntity<HttpHeaders>(null, null), String::class.java)
//    assert(result != null)
//    assert(result.hasBody())
//    assert(result.statusCode.is2xxSuccessful)
//    var stringcompanion = CapturedSpringConfigValues.objectMapper.readTree(result.body?.toString())
//    var version = stringcompanion.get("components").get("healthInfo").get("details").get("version")
//    assertThat(version.asText().equals("1_0_0") || version.asText().startsWith(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)))
//  }

//  @Test
//  fun `Health ping page is accessible`() {
//    val result = restTemplate.exchange("/health/ping", HttpMethod.GET, HttpEntity<HttpHeaders>(setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDITOR", "ROLE_EDUCATION_WORK_PLAN_VIEWER"))), String::class.java)
//    assert(result != null)
//    assert(result.hasBody())
//    assert(result.statusCode.is2xxSuccessful)
//    var stringcompanion = CapturedSpringConfigValues.objectMapper.readTree(result.body?.toString() ?: "")
//    var status = stringcompanion.get("status")
//    assertThat(status.asText().toString()).isEqualTo("UP")
//  }

//  @Test
//  fun `readiness reports ok`() {
//    val result = restTemplate.exchange("/health/readiness", HttpMethod.GET, HttpEntity<HttpHeaders>(setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDITOR", "ROLE_EDUCATION_WORK_PLAN_VIEWER"))), String::class.java)
//    assert(result != null)
//    assert(result.hasBody())
//    assert(result.statusCode.is2xxSuccessful)
//    var stringcompanion = CapturedSpringConfigValues.objectMapper.readTree(result.body?.toString() ?: "")
//    var status = stringcompanion.get("status")
//    assertThat(status.asText().toString()).isEqualTo("UP")
//  }

//  @Test
//  fun `liveness reports ok`() {
//    val result = restTemplate.exchange("/health/liveness", HttpMethod.GET, HttpEntity<HttpHeaders>(setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDITOR", "ROLE_EDUCATION_WORK_PLAN_VIEWER"))), String::class.java)
//    assert(result != null)
//    assert(result.hasBody())
//    assert(result.statusCode.is2xxSuccessful)
//    var stringcompanion = CapturedSpringConfigValues.objectMapper.readTree(result.body?.toString() ?: "")
//    var status = stringcompanion.get("status")
//    assertThat(status.asText().toString()).isEqualTo("UP")
//  }
}
