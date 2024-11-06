package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import java.util.UUID.randomUUID

object PostcodeMother {

  const val RELEASE_AREA_POSTCODE = "AG121RW"

  class Builder {
    var id: EntityId = EntityId(randomUUID().toString())
    var code: String = ""
    var xCoordinate: Double? = null
    var yCoordinate: Double? = null

    val postcodeCoordinates: MutableMap<String, Pair<Double, Double>> = mutableMapOf(
      RELEASE_AREA_POSTCODE to Pair(0.0, 0.0),
      abcConstructionApprentice.postcode to Pair(25000.0, 25000.0),
      amazonForkliftOperator.postcode to Pair(22800.0, 22800.0),
      tescoWarehouseHandler.postcode to Pair(1100.0, 1100.0),
    )

    fun from(postcode: String): Builder {
      this.code = postcode
      this.xCoordinate = postcodeCoordinates[postcode]?.first
      this.yCoordinate = postcodeCoordinates[postcode]?.second
      return this
    }

    fun build(): Postcode {
      return Postcode(
        id = this.id,
        code = this.code,
        xCoordinate = this.xCoordinate,
        yCoordinate = this.yCoordinate,
      )
    }
  }
}
