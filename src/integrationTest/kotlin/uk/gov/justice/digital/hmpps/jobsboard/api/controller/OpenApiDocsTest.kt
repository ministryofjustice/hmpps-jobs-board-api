package uk.gov.justice.digital.hmpps.jobsboard.api.controller

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.parser.OpenAPIV3Parser
import net.minidev.json.JSONArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.ApplicationTestCase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OpenApiDocsTest : ApplicationTestCase() {
  @LocalServerPort
  private val port: Int = 0

  @Autowired
  protected lateinit var webTestClient: WebTestClient

  @Test
  fun `open api docs are available`() {
    webTestClient.get()
      .uri("/swagger-ui/index.html?configUrl=/v3/api-docs")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
  }

  @Test
  fun `open api docs redirect to correct page`() {
    webTestClient.get()
      .uri("/swagger-ui.html")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().is3xxRedirection
      .expectHeader().value("Location") { it.contains("/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config") }
  }

  @Test
  fun `the open api json contains documentation`() {
    webTestClient.get()
      .uri("/v3/api-docs")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("paths").isNotEmpty
  }

  @Test
  fun `the open api json contains the version number`() {
    webTestClient.get()
      .uri("/v3/api-docs")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody().jsonPath("info.version").value<String> {
        assertThat(it).startsWith(DateTimeFormatter.ISO_DATE.format(LocalDate.now()))
      }
  }

  @Test
  fun `the open api json is valid`() {
    val result = OpenAPIV3Parser().readLocation("http://localhost:$port/v3/api-docs", null, null)
    assertThat(result.messages).isEmpty()
  }

  @Test
  fun `the open api json path security requirements are valid`() {
    val result = OpenAPIV3Parser().readLocation("http://localhost:$port/v3/api-docs", null, null)

    // TODO remove these exclusions when SQS Queue Admin endpoints have been enhanced to include Security reqs.
    val exclusions = setOf(
      "/queue-admin/retry-dlq/{dlqName}",
      "/queue-admin/retry-all-dlqs",
      "/queue-admin/purge-queue/{queueName}",
      "/queue-admin/get-dlq-messages/{dlqName}",
    )

    // The security requirements of each path don't appear to be validated like they are at https://editor.swagger.io/
    // We therefore need to grab all the valid security requirements and check that each path only contains those items
    val securityRequirements = result.openAPI.security.flatMap { it.keys }
    val checkSecurityReqs: (String, Operation) -> Unit = { path, op ->
      val pathAndOperation: () -> String = { "path: $path ; operation (${op.operationId} - ${op.summary ?: ""})" }
      assertThat(op.security).`as`(pathAndOperation()).isNotNull
      assertThat(op.security.flatMap { it.keys }).`as`(pathAndOperation()).isSubsetOf(securityRequirements)
    }
    result.openAPI.paths
      .filter { !exclusions.contains(it.key) }
      .forEach { pathItem ->
        pathItem.value.run { listOfNotNull(get, put, delete).forEach { checkSecurityReqs(pathItem.key, it) } }
      }
  }

  @ParameterizedTest
  @CsvSource(
    """
    view-jobs-board-role, ROLE_EDUCATION_WORK_PLAN_VIEW, read,
    edit-jobs-board-role, ROLE_EDUCATION_WORK_PLAN_EDIT, read, write
    view-employers-role, ROLE_JOBS_BOARD__EMPLOYERS__RO, read,
    view-jobs-role, ROLE_JOBS_BOARD__JOBS__RO, read,
    edit-jobs-eoi-role, ROLE_JOBS_BOARD__JOBS__EOI__RW, read, write""",
  )
  fun `the security scheme is setup for bearer tokens`(key: String, role: String, readScope: String?, writeScope: String?) {
    val expectedScopes = JSONArray().apply {
      listOfNotNull(readScope, writeScope).forEach { add(it) }
    }
    webTestClient.get()
      .uri("/v3/api-docs")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.components.securitySchemes.$key.type").isEqualTo("http")
      .jsonPath("$.components.securitySchemes.$key.scheme").isEqualTo("bearer")
      .jsonPath("$.components.securitySchemes.$key.description").value<String> {
        assertThat(it).contains(role)
      }
      .jsonPath("$.components.securitySchemes.$key.bearerFormat").isEqualTo("JWT")
      .jsonPath("$.security[0].$key").isEqualTo(expectedScopes)
  }

  @Test
  fun `all endpoints have a security scheme defined`() {
    // There are 4 SQS endpoints without security scheme, to be excluded from this test; all these endpoint has single tag "hmpps-queue-resource"
    val queueAdminTag = "hmpps-queue-resource"
    val sarTags = "Subject Access Request"
    val excludeCount = 4

    webTestClient.get()
      .uri("/v3/api-docs")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.paths[*][*][?(!@.security)]..tags[0]").value<JSONArray> {
        assertThat(it).hasSize(excludeCount)
        it.forEach { tag -> assertThat(tag).isIn(queueAdminTag) }
      }
  }
}
