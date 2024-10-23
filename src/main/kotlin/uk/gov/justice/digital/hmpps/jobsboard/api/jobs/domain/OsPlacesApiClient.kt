package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.OsPlacesApiDPA

interface OsPlacesApiClient {
  fun getAddressesFor(postcode: String): OsPlacesApiDPA
}
