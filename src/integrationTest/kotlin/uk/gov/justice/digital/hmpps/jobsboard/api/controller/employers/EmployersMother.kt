package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.util.*

object EmployersMother {
  private fun employerBody(name: String, description: String, sector: String, status: String): String = """
        {
          "name": "$name",
          "description": "$description",
          "sector": "$sector",
          "status": "$status"
        }
  """.trimIndent()

  val tesco = Employer(
    id = EntityId(UUID.randomUUID().toString()),
    name = "Tesco",
    description = "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
    sector = "RETAIL",
    status = "SILVER",
  )

  val tescoLogistics = Employer(
    id = EntityId(UUID.randomUUID().toString()),
    name = "Tesco",
    description = "This is another Tesco employer that provides logistic services.",
    sector = "LOGISTICS",
    status = "GOLD",
  )

  val sainsburys = Employer(
    id = EntityId(UUID.randomUUID().toString()),
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century.",
    sector = "RETAIL",
    status = "GOLD",
  )

  val amazon = Employer(
    id = EntityId(UUID.randomUUID().toString()),
    name = "Amazon",
    description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
    sector = "LOGISTICS",
    status = "KEY_PARTNER",
  )

  val abcConstruction = Employer(
    id = EntityId(UUID.randomUUID().toString()),
    name = "ABC Construction",
    description = "This is a description",
    sector = "CONSTRUCTION",
    status = "SILVER",
  )

  val Employer.requestBody: String get() = employerBody(name, description, sector, status)
}
