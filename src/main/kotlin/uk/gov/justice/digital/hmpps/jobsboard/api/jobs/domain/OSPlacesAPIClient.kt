package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.OSPlacesAPIDPA

interface OSPlacesAPIClient {
  fun getAddressesFor(postcode: String): OSPlacesAPIDPA
}
