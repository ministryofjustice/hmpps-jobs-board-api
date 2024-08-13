package uk.gov.justice.digital.hmpps.jobsboard.api

import org.awaitility.Awaitility
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.data.auditing.AuditingHandler
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.PUT
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
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EMPLOYERS_ENDPOINT
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.helpers.JwtAuthHelper
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import uk.gov.justice.digital.hmpps.jobsboard.api.time.DefaultTimeProvider
import java.time.Instant
import java.util.*
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

@ExtendWith(SpringExtension::class)
@SpringBootTest(
  webEnvironment = RANDOM_PORT,
  classes = [HmppsJobsBoardApi::class],
)
@AutoConfigureTestDatabase(replace = NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test-containers-flyway")
abstract class ApplicationTestCase {

  @Autowired
  private lateinit var flyway: Flyway

  @Autowired
  private lateinit var employerRepository: EmployerRepository

  @Autowired
  private lateinit var jobRepository: JobRepository

  @MockBean
  protected lateinit var timeProvider: DefaultTimeProvider

  @MockBean
  protected lateinit var dateTimeProvider: DateTimeProvider

  @SpyBean
  protected lateinit var auditingHandler: AuditingHandler

  @Autowired
  protected lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var jwtAuthHelper: JwtAuthHelper

  val jobCreationTime = Instant.parse("2024-01-01T00:00:00Z")

  companion object {
    private val postgresContainer = PostgresContainer.flywayContainer

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

  @BeforeAll
  fun beforeAll() {
    flyway.clean()
    flyway.migrate()
    auditingHandler.setDateTimeProvider(dateTimeProvider)
    whenever(dateTimeProvider.now).thenReturn(Optional.of(jobCreationTime))
  }

  @BeforeEach
  fun setup() {
    jobRepository.deleteAll()
    employerRepository.deleteAll()
    whenever(timeProvider.now()).thenCallRealMethod()
  }

  internal fun setAuthorisation(
    user: String = "test-client",
    roles: List<String> = listOf(),
  ): (HttpHeaders) {
    return jwtAuthHelper.setAuthorisationForUnitTests(user, roles)
  }

  private fun httpHeaders(): HttpHeaders = this.setAuthorisation(roles = listOf("ROLE_EDUCATION_WORK_PLAN_EDIT"))

  protected fun newEmployerBody(name: String, description: String, sector: String, status: String): String = """
        {
          "name": "$name",
          "description": "$description",
          "sector": "$sector",
          "status": "$status"
        }
  """.trimIndent()

  val tescoBody: String = newEmployerBody(
    name = "Tesco",
    description = "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
    sector = "RETAIL",
    status = "SILVER",
  )

  val tescoLogisticsBody: String = newEmployerBody(
    name = "Tesco",
    description = "This is another Tesco employer that provides logistic services.",
    sector = "LOGISTICS",
    status = "GOLD",
  )

  val sainsburysBody: String = newEmployerBody(
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century.",
    sector = "RETAIL",
    status = "GOLD",
  )

  val amazonBody: String = newEmployerBody(
    name = "Amazon",
    description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
    sector = "LOGISTICS",
    status = "KEY_PARTNER",
  )

  protected fun assertAddEmployer(
    id: String? = null,
    body: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
  ): String {
    val employerId = id ?: randomUUID().toString()
    assertRequestWithBody(
      url = "$EMPLOYERS_ENDPOINT/$employerId",
      body = body,
      expectedStatus = expectedStatus,
      expectedResponse = expectedResponse,
    )
    return employerId
  }

  protected fun assertRequestWithBody(
    url: String,
    body: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
  ) {
    val resultActions = mockMvc.perform(
      request(PUT, url)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .content(body)
        .headers(httpHeaders()),
    ).andExpect(status().isEqualTo(expectedStatus.value()))
    expectedResponse?.let {
      resultActions.andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(it))
    }
  }

  protected fun assertResponse(
    url: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
    expectedNameSortedList: List<String>? = null,
    expectedDateSortedList: List<String>? = null,
  ) {
    val resultActions = mockMvc.get(url) {
      contentType = APPLICATION_JSON
      accept = APPLICATION_JSON
      headers {
        httpHeaders().forEach { (name, values) ->
          values.forEach { value ->
            header(name, value)
          }
        }
      }
    }.andExpect {
      status { isEqualTo(expectedStatus.value()) }
      content {
        contentType(APPLICATION_JSON)
        expectedResponse?.let {
          json(expectedResponse)
        }
        expectedNameSortedList?.let {
          jsonPath("$.content[0].name", equalTo(it[0]))
          jsonPath("$.content[1].name", equalTo(it[1]))
        }
        expectedDateSortedList?.let {
          jsonPath("$.content[0].createdAt", equalTo(it[0]))
          jsonPath("$.content[1].createdAt", equalTo(it[1]))
        }
      }
    }
  }
}
