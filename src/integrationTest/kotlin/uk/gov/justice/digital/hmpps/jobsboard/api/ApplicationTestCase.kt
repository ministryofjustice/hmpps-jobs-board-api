package uk.gov.justice.digital.hmpps.jobsboard.api

import org.awaitility.Awaitility
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.isEqualTo
import uk.gov.justice.digital.hmpps.jobsboard.api.helpers.JwtAuthHelper
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import uk.gov.justice.digital.hmpps.jobsboard.api.time.DefaultTimeProvider
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

@ExtendWith(SpringExtension::class)
@SpringBootTest(
  webEnvironment = RANDOM_PORT,
  classes = [HmppsJobsBoardApi::class, TestConfig::class],
)
@AutoConfigureMockMvc
@ActiveProfiles("test-containers-flyway")
abstract class ApplicationTestCase {

  @Autowired
  private lateinit var employerRepository: EmployerRepository

  @MockBean
  protected lateinit var timeProvider: DefaultTimeProvider

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var jwtAuthHelper: JwtAuthHelper

  companion object {
    private val postgresContainer = PostgresContainer.instance

    @JvmStatic
    @DynamicPropertySource
    fun configureTestContainers(registry: DynamicPropertyRegistry) {
      postgresContainer?.run {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
        registry.add("spring.datasource.username", postgresContainer::getUsername)
        registry.add("spring.datasource.password", postgresContainer::getPassword)
        registry.add("spring.flyway.url", postgresContainer::getJdbcUrl)
        registry.add("spring.flyway.user", postgresContainer::getUsername)
        registry.add("spring.flyway.password", postgresContainer::getPassword)
      }
    }
  }

  init {
    Awaitility.setDefaultPollInterval(500, MILLISECONDS)
    Awaitility.setDefaultTimeout(3, SECONDS)
  }

  @BeforeEach
  fun setup() {
    employerRepository.deleteAll()
    whenever(timeProvider.now()).thenCallRealMethod()
  }

  internal fun setAuthorisation(
    user: String = "test-client",
    roles: List<String> = listOf(),
  ): (HttpHeaders) {
    return jwtAuthHelper.setAuthorisationForUnitTests(user, roles)
  }

  fun assertRequestWithBody(
    method: HttpMethod,
    endpoint: String,
    body: String,
    expectedStatus: HttpStatus,
  ) {
    val httpHeaders: HttpHeaders = this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDIT"))
    mockMvc.perform(
      request(method, endpoint)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .content(body)
        .headers(httpHeaders),
    ).andExpect(status().isEqualTo(expectedStatus.value()))
  }

  @Throws(Exception::class)
  fun assertResponse(
    endpoint: String,
    expectedStatus: HttpStatus,
    expectedResponse: String,
  ) {
    val httpHeaders: HttpHeaders = this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_VIEW"))
    mockMvc.get(endpoint) {
      contentType = APPLICATION_JSON
      accept = APPLICATION_JSON
      headers {
        httpHeaders.forEach { (name, values) ->
          values.forEach { value ->
            header(name, value)
          }
        }
      }
    }.andExpect {
      status { isEqualTo(expectedStatus.value()) }
      content {
        contentType(APPLICATION_JSON)
        json(expectedResponse)
      }
    }
  }

  @Throws(Exception::class)
  fun assertSortedByNameResponse(
    endpoint: String,
    expectedStatus: HttpStatus,
    expectedOrderedContentNames: List<String>,
  ) {
    val httpHeaders: HttpHeaders = this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_VIEW"))
    mockMvc.get(endpoint) {
      contentType = APPLICATION_JSON
      accept = APPLICATION_JSON
      headers {
        httpHeaders.forEach { (name, values) ->
          values.forEach { value ->
            header(name, value)
          }
        }
      }
    }.andExpect {
      status { isEqualTo(expectedStatus.value()) }
      content {
        contentType(APPLICATION_JSON)
        jsonPath("$.content[0].name", equalTo(expectedOrderedContentNames[0]))
        jsonPath("$.content[1].name", equalTo(expectedOrderedContentNames[1]))
      }
    }
  }

  @Throws(Exception::class)
  fun assertSortedByDateResponse(
    endpoint: String,
    expectedStatus: HttpStatus,
    expectedOrderedContentDates: List<String>,
  ) {
    val httpHeaders: HttpHeaders = this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_VIEW"))
    mockMvc.get(endpoint) {
      contentType = APPLICATION_JSON
      accept = APPLICATION_JSON
      headers {
        httpHeaders.forEach { (name, values) ->
          values.forEach { value ->
            header(name, value)
          }
        }
      }
    }.andExpect {
      status { isEqualTo(expectedStatus.value()) }
      content {
        contentType(APPLICATION_JSON)
        jsonPath("$.content[0].createdAt", equalTo(expectedOrderedContentDates[0]))
        jsonPath("$.content[1].createdAt", equalTo(expectedOrderedContentDates[1]))
      }
    }
  }

  fun assertErrorRequestWithBody(
    method: HttpMethod,
    endpoint: String,
    body: String,
    expectedStatus: HttpStatus,
    expectedErrorResponse: String,
  ) {
    val httpHeaders: HttpHeaders = this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDIT"))
    mockMvc.perform(
      request(method, endpoint)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .content(body)
        .headers(httpHeaders),
    ).andExpect(status().isEqualTo(expectedStatus.value()))
      .andExpect(content().contentType(APPLICATION_JSON))
      .andExpect(content().json(expectedErrorResponse))
  }
}
