package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository

@Service
class PostcodeLocationService(
  private val postcodesRepository: PostcodesRepository,
) {
  fun save(postcode: String) {
    postcodesRepository.save(
      Postcode(
        id = EntityId(UUIDGenerator()),
        code = postcode,
        xCoordinate = 0.00,
        yCoordinate = 0.00,
      ),
    )
  }
}
