package uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

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
    name = "Tesco",
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
}
