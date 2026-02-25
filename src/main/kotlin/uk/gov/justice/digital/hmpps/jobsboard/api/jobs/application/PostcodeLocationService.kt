package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.OsPlacesApiClient
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository

@Service
class PostcodeLocationService(
  private val postcodesRepository: PostcodesRepository,
  private val osPlacesAPIClient: OsPlacesApiClient,
  private val uuidGenerator: UUIDGenerator,
) {
  fun save(postcode: String) {
    val savedPostcode = postcodesRepository.findByCode(postcode)

    savedPostcode?.let {
      if (it.xCoordinate != null && it.yCoordinate != null) return
    }

    val postcodeCoordinates = osPlacesAPIClient.getAddressesFor(postcode)

    val postcodeToSave = savedPostcode?.copy(
      xCoordinate = postcodeCoordinates.xCoordinate,
      yCoordinate = postcodeCoordinates.yCoordinate,
    ) ?: Postcode(
      id = EntityId(uuidGenerator),
      code = postcode,
      xCoordinate = postcodeCoordinates.xCoordinate,
      yCoordinate = postcodeCoordinates.yCoordinate,
    )

    postcodesRepository.save(postcodeToSave)
  }

  fun isGeoCoded(postcode: String?): Boolean {
    val pc = postcode?.trim()?.takeIf { it.isNotEmpty() } ?: return false

    postcodesRepository.findByCode(pc)?.let { saved ->
      return saved.xCoordinate != null && saved.yCoordinate != null
    }
    val dpa = runCatching {
      osPlacesAPIClient.getAddressesFor(pc)
    }.getOrNull() ?: return false

    return dpa.xCoordinate != null && dpa.yCoordinate != null
  }
}
