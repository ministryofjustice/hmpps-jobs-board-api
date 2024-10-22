package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.OSPlacesAPIClient
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository

@Service
class PostcodeLocationService(
  private val postcodesRepository: PostcodesRepository,
  private val osPlacesAPIClient: OSPlacesAPIClient,
  private val uuidGenerator: UUIDGenerator,
) {
  fun save(postcode: String) {
    val postcodeCoordinates = osPlacesAPIClient.getAddressesFor(postcode)

    postcodesRepository.save(
      Postcode(
        id = EntityId(uuidGenerator),
        code = postcode,
        xCoordinate = postcodeCoordinates.xCoordinate,
        yCoordinate = postcodeCoordinates.yCoordinate,
      ),
    )
  }
}
