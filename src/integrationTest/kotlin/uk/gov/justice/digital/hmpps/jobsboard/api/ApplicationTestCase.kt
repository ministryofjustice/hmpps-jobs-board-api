package uk.gov.justice.digital.hmpps.jobsboard.api

import com.jayway.jsonpath.JsonPath
import org.awaitility.Awaitility
import org.flywaydb.core.Flyway
import org.hamcrest.Matchers.contains
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
import org.springframework.http.HttpMethod
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.isEqualTo
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers.EMPLOYERS_ENDPOINT
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.helpers.JwtAuthHelper
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers.PostgresContainer
import uk.gov.justice.digital.hmpps.jobsboard.api.time.DefaultTimeProvider
import java.security.SecureRandom
import java.time.Instant
import java.time.Period
import java.time.ZonedDateTime
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

  private val countOfGettingCurrentTime = intArrayOf(0)

  val defaultCurrentTime = Instant.parse("2024-01-01T00:00:00Z")

  private object Holder {
    val random: SecureRandom by lazy { SecureRandom() }
  }

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
  }

  @BeforeEach
  fun setup() {
    whenever(timeProvider.now()).thenCallRealMethod()
    whenever(dateTimeProvider.now).thenReturn(Optional.of(defaultCurrentTime))
    countOfGettingCurrentTime[0] = 0
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

  val abcConstructionBody: String = newEmployerBody(
    name = "ABC Construction",
    description = "This is a description",
    sector = "CONSTRUCTION",
    status = "SILVER",
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

  protected fun assertRequestWithoutBody(
    url: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
    expectedHttpVerb: HttpMethod,
    expectedRedirectUrlPattern: String? = null,
  ) {
    val resultActions = mockMvc.perform(
      request(expectedHttpVerb, url)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .headers(httpHeaders()),
    ).andExpect(status().isEqualTo(expectedStatus.value()))
    expectedResponse?.let {
      resultActions.andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(it))
    }
    expectedRedirectUrlPattern?.let {
      resultActions.andExpect(redirectedUrlPattern(expectedRedirectUrlPattern))
    }
  }

  protected fun assertResponse(
    url: String,
    expectedStatus: HttpStatus,
    expectedResponse: String? = null,
    expectedJobTitleSortedList: List<String>? = null,
    expectedNameSortedList: List<String>? = null,
    expectedDateSortedList: List<String>? = null,
    expectedDateSortingOrder: String? = null,
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
    }
    resultActions.andExpect {
      status { isEqualTo(expectedStatus.value()) }
      content {
        contentType(APPLICATION_JSON)
        expectedResponse?.let {
          json(expectedResponse)
        }
        expectedNameSortedList?.let { sortedList ->
          sortedList.forEachIndexed { index, expectedName ->
            jsonPath("$.content[$index].name", equalTo(expectedName))
          }
        }
        expectedJobTitleSortedList?.let { sortedList ->
          sortedList.forEachIndexed { index, expectedJobTitle ->
            jsonPath("$.content[$index].jobTitle", equalTo(expectedJobTitle))
          }
        }
        expectedDateSortedList?.let { sortedList ->
          sortedList.forEachIndexed { index, expectedDate ->
            jsonPath("$.content[$index].createdAt", equalTo(expectedDate))
          }
        }
        expectedDateSortingOrder?.let { sortingOrder ->
          val timestamps = (
            JsonPath.parse(resultActions.andReturn().response.contentAsString)
              .read("$.content[*].createdAt") as List<String>
            ).map { timestamp ->
            ZonedDateTime.parse(timestamp).toInstant()
          }.run {
            when (sortingOrder) {
              "asc" -> this.sorted()
              "desc" -> this.sortedDescending()
              else -> this
            }
          }.map { instant ->
            instant.toString()
          }.toTypedArray()

          resultActions.andExpect {
            content {
              jsonPath("$.content[*].createdAt", contains(*timestamps))
            }
          }
        }
      }
    }
  }

  protected fun expectedResponseListOf(vararg elements: String): String {
    return expectedResponseListOf(10, 0, elements = elements)
  }

  protected fun expectedResponseListOf(size: Int, page: Int, vararg elements: String): String {
    return expectedResponseListOf(size, page, elements.size, *elements)
  }

  protected fun expectedResponseListOf(size: Int, page: Int, totalElements: Int, vararg elements: String): String {
    val totalPages = (totalElements + size - 1) / size
    val expectedResponse = """
         {
          "content": [ ${elements.joinToString(separator = ",")}],
          "page": {
            "size": $size,
            "number": $page,
            "totalElements": $totalElements,
            "totalPages": $totalPages
          }
        }
    """.trimIndent()
    return expectedResponse
  }

  protected fun givenCurrentTimeIsStrictlyIncreasing(startTime: Instant) {
    whenever(dateTimeProvider.now)
      .thenAnswer { Optional.of(startTime.plusSeconds((this.countOfGettingCurrentTime[0]++ * 10).toLong())) }
  }

  protected fun givenCurrentTimeIsStrictlyIncreasingIncrementByDay(startTime: Instant) {
    whenever(dateTimeProvider.now)
      .thenAnswer { Optional.of(startTime.plus(Period.ofDays(this.countOfGettingCurrentTime[0]++))) }
  }

  protected fun randomAlphabets(length: Int): String =
    ('A'..'Z').let { alphabets -> String(CharArray(length) { alphabets.random() }) }

  protected fun randomDigits(length: Int): String =
    String(CharArray(length) { Holder.random.nextInt(9 + 1).toString()[0] })
}
