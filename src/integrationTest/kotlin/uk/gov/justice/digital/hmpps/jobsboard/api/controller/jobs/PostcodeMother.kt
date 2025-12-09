package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.asdaWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import java.util.UUID.randomUUID

object PostcodeMother {

  const val RELEASE_AREA_POSTCODE = "AG121RW"
  const val NO_FIXED_ABODE_POSTCODE = "NF1 1NF"

  val postcodeMap = listOf(
    postcode(RELEASE_AREA_POSTCODE, 0.0, 0.0),
    postcode(abcConstructionApprentice.postcode!!, 25000.0, 25000.0),
    postcode(amazonForkliftOperator.postcode!!, 22800.0, 22800.0),
    postcode(tescoWarehouseHandler.postcode!!, 1100.0, 1100.0),
    postcode("LS110AN", 429017.0, 431869.0),
    postcode("LS11 0AN", 429017.0, 431869.0),
    postcode("M4 5BD", 385003.00, 398558.00),
    postcode("NW1 6XE", 527870.40, 182081.17),
    postcode("NG1 1AA", 457804.00, 340087.00),
    postcode("E1 6AN", 533397.00, 181741.00),
    postcode(asdaWarehouseHandler.postcode!!, null, null),
  ).associateBy({ it.code }, { it })

  private fun postcode(postcode: String, xCoordinate: Double?, yCoordinate: Double?) = Postcode(EntityId(), postcode, xCoordinate, yCoordinate)

  class Builder {
    var id: EntityId = EntityId(randomUUID().toString())
    var code: String = ""
    var xCoordinate: Double? = null
    var yCoordinate: Double? = null

    val postcodeCoordinates: MutableMap<String, Pair<Double, Double>> = mutableMapOf(
      RELEASE_AREA_POSTCODE to Pair(0.0, 0.0),
      abcConstructionApprentice.postcode!! to Pair(25000.0, 25000.0),
      amazonForkliftOperator.postcode!! to Pair(22800.0, 22800.0),
      tescoWarehouseHandler.postcode!! to Pair(1100.0, 1100.0),
    )

    fun from(postcode: String): Builder {
      this.code = postcode
      this.xCoordinate = postcodeCoordinates[postcode]?.first
      this.yCoordinate = postcodeCoordinates[postcode]?.second
      return this
    }

    fun build(): Postcode = Postcode(
      id = this.id,
      code = this.code,
      xCoordinate = this.xCoordinate,
      yCoordinate = this.yCoordinate,
    )
  }
}
