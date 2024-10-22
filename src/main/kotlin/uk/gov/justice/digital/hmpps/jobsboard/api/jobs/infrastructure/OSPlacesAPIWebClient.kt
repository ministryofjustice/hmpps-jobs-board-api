package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.OSPlacesAPIClient

@Service
class OSPlacesAPIWebClient : OSPlacesAPIClient {
  override fun getAddressesFor(postcode: String): OSPlacesAPIDPA {
    return OSPlacesAPIDPA(
      postcode = "",
      xCoordinate = 0.00,
      yCoordinate = 0.00,
    )
  }
}
