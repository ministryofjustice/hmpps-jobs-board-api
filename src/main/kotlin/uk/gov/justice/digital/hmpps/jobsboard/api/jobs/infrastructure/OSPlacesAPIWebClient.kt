package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

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
    return OSPlacesAPIDPA(
      postcode = "",
      xCoordinate = 0.00,
      yCoordinate = 0.00,
    )
  }
}
