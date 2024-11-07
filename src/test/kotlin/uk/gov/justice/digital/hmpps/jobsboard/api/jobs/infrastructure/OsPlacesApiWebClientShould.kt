package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.jobsboard.api.config.OsPlacesApiProperties
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator

@ExtendWith(MockitoExtension::class)
class OsPlacesApiWebClientShould {
  @Mock
  private lateinit var osPlacesWebClient: WebClient

  @Mock
  lateinit var osPlacesAPIProperties: OsPlacesApiProperties

  @InjectMocks
  private lateinit var osPlacesAPIWebClient: OsPlacesApiWebClient

  companion object {
    val API_KEY = "test-api-key"
  }

  @BeforeEach
  fun setup() {
    whenever(osPlacesAPIProperties.key).thenReturn(API_KEY)
  }

  @Test
  fun `get coordinates when a valid postcode is provided`() {
    val expectedPostcode = OsPlacesApiDPA(
      postcode = amazonForkliftOperator.postcode,
      xCoordinate = 1.23,
      yCoordinate = 4.56,
    )
    val expectedSearchResult = OsPlacesApiResponse(
      results = listOf(
        OsPlacesApiAddress(
          dpa = expectedPostcode,
        ),
      ),
    )
    val requestUriMock = mock(WebClient.RequestHeadersUriSpec::class.java)
    val requestHeadersMock = mock(WebClient.RequestHeadersSpec::class.java)
    val responseSpecMock = mock(WebClient.ResponseSpec::class.java)
    whenever(osPlacesWebClient.get()).thenReturn(requestUriMock)
    whenever(requestUriMock.uri("/postcode?postcode=${amazonForkliftOperator.postcode}&key=$API_KEY"))
      .thenReturn(requestHeadersMock)
    whenever(requestHeadersMock.accept(APPLICATION_JSON)).thenReturn(requestHeadersMock)
    whenever(requestHeadersMock.retrieve()).thenReturn(responseSpecMock)
    whenever(responseSpecMock.bodyToMono(OsPlacesApiResponse::class.java))
      .thenReturn(Mono.just(expectedSearchResult))

    val postcode = osPlacesAPIWebClient.getAddressesFor(amazonForkliftOperator.postcode)

    assertThat(postcode).isEqualTo(expectedPostcode)
  }
}
