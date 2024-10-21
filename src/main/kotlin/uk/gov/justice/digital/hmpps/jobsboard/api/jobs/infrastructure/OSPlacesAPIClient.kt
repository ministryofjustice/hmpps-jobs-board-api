package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.springframework.stereotype.Service

@Service
class OSPlacesAPIClient {
  fun getAddressesFor(postcode: String): OSPlacesAPIDPA {
    return OSPlacesAPIDPA(
      postcode = "",
      xCoordinate = 0.00,
      yCoordinate = 0.00,
    )
  }
}
