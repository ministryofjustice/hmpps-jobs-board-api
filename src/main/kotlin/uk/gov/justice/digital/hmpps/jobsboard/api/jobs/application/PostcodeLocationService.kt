package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import java.util.*

class PostcodeLocationService {
  fun getPostcodeFrom(postcode: String): Postcode {
    return Postcode(
      id = EntityId(UUID.fromString(postcode).toString()),
      code = postcode,
      xCoordinate = 0.00f,
      yCoordinate = 0.00f,
    )
  }
}
