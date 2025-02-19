package uk.gov.justice.digital.hmpps.jobsboard.api.controller.employers

import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.employerCreationTime
import java.time.Instant
import java.util.*

object EmployerMother {
  val tesco = Employer(
    id = EntityId("89de6c84-3372-4546-bbc1-9d1dc9ceb354"),
    name = "Tesco",
    description = "Tesco plc is a British multinational groceries and general merchandise retailer headquartered in Welwyn Garden City, England. The company was founded by Jack Cohen in Hackney, London in 1919.",
    sector = "RETAIL",
    status = "SILVER",
  )

  val tescoLogistics = Employer(
    id = EntityId("2c8032bf-e583-4ae9-bcec-968a1c4881f9"),
    name = "Tesco Logistics",
    description = "This is another Tesco employer that provides logistic services.",
    sector = "LOGISTICS",
    status = "GOLD",
  )

  val sainsburys = Employer(
    id = EntityId("f4fbdbf3-823c-4877-aafc-35a7fa74a15a"),
    name = "Sainsbury's",
    description = "J Sainsbury plc, trading as Sainsbury's, is a British supermarket and the second-largest chain of supermarkets in the United Kingdom. Founded in 1869 by John James Sainsbury with a shop in Drury Lane, London, the company was the largest UK retailer of groceries for most of the 20th century.",
    sector = "RETAIL",
    status = "GOLD",
  )

  val amazon = Employer(
    id = EntityId("bf392249-b360-4e3e-81a0-8497047987e8"),
    name = "Amazon",
    description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
    sector = "LOGISTICS",
    status = "KEY_PARTNER",
  )

  val abcConstruction = Employer(
    id = EntityId("182e9a24-6edb-48a6-a84f-b7061f004a97"),
    name = "ABC Construction",
    description = "This is a description",
    sector = "CONSTRUCTION",
    status = "SILVER",
  )

  fun builder(): EmployerBuilder = EmployerBuilder()

  val Employer.requestBody: String get() = employerRequestBody(name, description, sector, status)
  val Employer.responseBody: String get() = employerResponseBody(id, name, description, sector, status)

  private fun employerRequestBody(name: String, description: String, sector: String, status: String): String = employerBody(name, description, sector, status)

  private fun employerResponseBody(id: EntityId, name: String, description: String, sector: String, status: String): String = employerBody(name, description, sector, status, id.id)

  private fun employerBody(
    name: String,
    description: String,
    sector: String,
    status: String,
    id: String? = null,
  ): String {
    val idField = id?.let { "\"id\": \"$it\"," } ?: ""
    return """
        {
          $idField
          "name": "$name",
          "description": "$description",
          "sector": "$sector",
          "status": "$status"
        }
    """.trimIndent()
  }
}

class EmployerBuilder {
  var id: EntityId = EntityId(UUID.randomUUID().toString())
  var name: String = ""
  var description: String = ""
  var sector: String = ""
  var status: String = ""
  var jobs: List<Job> = emptyList()
  var createdAt: Instant? = employerCreationTime

  fun from(employer: Employer): EmployerBuilder {
    this.id = employer.id
    this.name = employer.name
    this.description = employer.description
    this.sector = employer.sector
    this.status = employer.status
    this.jobs = employer.jobs
    this.createdAt = employer.createdAt
    return this
  }

  fun withName(name: String): EmployerBuilder {
    this.name = name
    return this
  }

  fun build(): Employer = Employer(
    id = this.id,
    name = this.name,
    description = this.description,
    sector = this.sector,
    status = this.status,
    jobs = this.jobs,
  )
}
