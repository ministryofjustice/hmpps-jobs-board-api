package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.slf4j.LoggerFactory
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
  private val log = LoggerFactory.getLogger(this::class.java)

  override fun getAddressesFor(postcode: String): OsPlacesApiDPA {
    val uri = "/postcode?postcode=$postcode&key=${osPlacesAPIProperties.key}"
    log.debug("Calling operation for $uri")

    return try {
      val searchResult = osPlacesWebClient
        .get()
        .uri(uri)
        .accept(APPLICATION_JSON)
        .retrieve()
        .bodyToMono(OsPlacesApiResponse::class.java)
        .block()

      searchResult?.results?.first()?.dpa ?: OsPlacesApiDPA(
        postcode = postcode,
        xCoordinate = null,
        yCoordinate = null,
      )
    } catch (exception: Exception) {
      OsPlacesApiDPA(
        postcode = postcode,
        xCoordinate = null,
        yCoordinate = null,
      )
    }
  }
}
