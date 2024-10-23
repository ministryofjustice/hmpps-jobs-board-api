package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.jobsboard.api.config.OsPlacesApiProperties
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.OsPlacesApiClient

@Service
class OsPlacesApiWebClient(
  private val osPlacesWebClient: WebClient,
  private val osPlacesAPIProperties: OsPlacesApiProperties,
) : OsPlacesApiClient {
  override fun getAddressesFor(postcode: String): OsPlacesApiDPA {
    val searchResult = osPlacesWebClient
      .get()
      .uri("/postcode?postcode=$postcode&key=${osPlacesAPIProperties.key}")
      .accept(APPLICATION_JSON)
      .retrieve()
      .bodyToMono(OsPlacesApiResponse::class.java)
      .block()

    return searchResult?.results?.first()?.dpa ?: OsPlacesApiDPA(
      postcode = postcode,
      xCoordinate = 0.00f,
      yCoordinate = 0.00f,
    )
  }
}
