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
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import uk.gov.justice.digital.hmpps.jobsboard.api.config.OSPlacesAPIProperties
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator

@ExtendWith(MockitoExtension::class)
class OSPlacesAPIWebClientShould {
  @Mock
  private lateinit var osPlacesWebClient: WebClient

  @Mock
  lateinit var osPlacesAPIProperties: OSPlacesAPIProperties

  @InjectMocks
  private lateinit var osPlacesAPIWebClient: OSPlacesAPIWebClient

  @BeforeEach
  fun setup() {
    whenever(osPlacesAPIProperties.key).thenReturn("test-api-key")
    whenever(osPlacesAPIProperties.url).thenReturn("https://api.os.uk")
  }

  @Test
  fun `get coordinates when a valid postcode is provided`() {
    val expectedPostcode = OSPlacesAPIDPA(
      postcode = amazonForkliftOperator.postcode,
      xCoordinate = 1.23,
      yCoordinate = 4.56,
    )
    val requestUriMock = mock(WebClient.RequestHeadersUriSpec::class.java)
    val requestHeadersMock = mock(WebClient.RequestHeadersSpec::class.java)
    val responseSpecMock = mock(WebClient.ResponseSpec::class.java)
    whenever(osPlacesWebClient.get()).thenReturn(requestUriMock)
    whenever(requestUriMock.uri("/postcodes/${amazonForkliftOperator.postcode}")).thenReturn(requestHeadersMock)
    whenever(requestHeadersMock.retrieve()).thenReturn(responseSpecMock)
    whenever(responseSpecMock.bodyToMono(OSPlacesAPIDPA::class.java)).thenReturn(Mono.just(expectedPostcode))

    val postcode = osPlacesAPIWebClient.getAddressesFor(amazonForkliftOperator.postcode)

    assertThat(postcode).isEqualTo(expectedPostcode)
  }
}
