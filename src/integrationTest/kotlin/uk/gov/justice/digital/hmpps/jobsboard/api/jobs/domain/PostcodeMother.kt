package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

object PostcodeMother {
  val postcodeMap = listOf(
    postcode("LS12", 428877.0, 432882.0),
    postcode("NE157LR", 418688.0, 565599.0),
    postcode("LS110AN", 429017.0, 431869.0),
    postcode("M4 5BD", 385003.00, 398558.00),
    postcode("NW1 6XE", 527870.40, 182081.17),
    postcode("NG1 1AA", 457804.00, 340087.00),
    postcode("E1 6AN", 533397.00, 181741.00),
  ).associateBy({ it.code }, { it })

  private fun postcode(postcode: String, xCoordinate: Double, yCoordinate: Double) =
    Postcode(EntityId(), postcode, xCoordinate, yCoordinate)
}
