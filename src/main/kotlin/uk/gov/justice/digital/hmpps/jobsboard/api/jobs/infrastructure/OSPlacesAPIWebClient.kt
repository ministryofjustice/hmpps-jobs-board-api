package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.jobsboard.api.config.OSPlacesAPIProperties
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.OSPlacesAPIClient

@Service
class OSPlacesAPIWebClient(
  private val osPlacesWebClient: WebClient,
  private val osPlacesAPIProperties: OSPlacesAPIProperties,
) : OSPlacesAPIClient {
  override fun getAddressesFor(postcode: String): OSPlacesAPIDPA {
    val searchResult = osPlacesWebClient
      .get()
      .uri("/postcode?postcode=$postcode&key=${osPlacesAPIProperties.key}")
      .accept(APPLICATION_JSON)
      .retrieve()
      .bodyToMono(OSPlacesAPIResponse::class.java)
      .block()

    return searchResult?.results?.first()?.dpa ?: OSPlacesAPIDPA(
      postcode = postcode,
      xCoordinate = 0.00f,
      yCoordinate = 0.00f,
    )
  }
}
