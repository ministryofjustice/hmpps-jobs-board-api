package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.time.Instant
import java.time.LocalDate
import java.time.Month.JULY

class TestPrototypes {
  companion object {
    val defaultCreationTime = Instant.parse("2024-01-01T00:00:00Z")
    val jobCreationTime = Instant.parse("2024-01-01T00:00:00Z")
    val jobModificationTime = Instant.parse("2025-02-02T01:00:00Z")

    val amazonEmployer = Employer(
      id = EntityId("eaf7e96e-e45f-461d-bbcb-fd4cedf0499c"),
      name = "Amazon",
      description = "Amazon.com, Inc., doing business as Amazon, is an American multinational technology company, engaged in e-commerce, cloud computing, online advertising, digital streaming, and artificial intelligence.",
      sector = "LOGISTICS",
      status = "KEY_PARTNER",
    )

    val amazonForkliftOperatorJob = Job(
      id = EntityId("fe5d5175-5a21-4cec-a30b-fd87a5f76ce7"),
      title = "Forklift operator",
      sector = "WAREHOUSING",
      industrySector = "LOGISTICS",
      numberOfVacancies = 2,
      sourcePrimary = "PEL",
      sourceSecondary = "",
      charityName = "",
      postcode = "LS12",
      salaryFrom = 11.93f,
      salaryTo = 15.90f,
      salaryPeriod = "PER_HOUR",
      additionalSalaryInformation = "",
      isPayingAtLeastNationalMinimumWage = false,
      workPattern = "FLEXIBLE_SHIFTS",
      hoursPerWeek = "FULL_TIME",
      contractType = "TEMPORARY",
      baseLocation = "WORKPLACE",
      essentialCriteria = "",
      desirableCriteria = "",
      description = """
        What's on offer:
  
        - 5 days over 7, 05:30 to 15:30
        - Paid weekly
        - Immediate starts available
        - Full training provided
  
        Your duties will include:
  
        - Manoeuvring forklifts safely in busy industrial environments
        - Safely stacking and unstacking large quantities of goods onto shelves or pallets
        - Moving goods from storage areas to loading areas for transport
        - Unloading deliveries and safely relocating the goods to their designated storage areas
        - Ensuring forklift driving areas are free from spills or obstructions
        - Regularly checking forklift equipment for faults or damages
        - Consolidating partial pallets for incoming goods
      """.trimIndent(),
      offenceExclusions = "NONE,DRIVING",
      isRollingOpportunity = false,
      closingDate = LocalDate.of(2024, JULY, 20),
      isOnlyForPrisonLeavers = true,
      startDate = LocalDate.of(2024, JULY, 20),
      howToApply = "",
      supportingDocumentationRequired = "CV,DISCLOSURE_LETTER",
      supportingDocumentationDetails = "",
      employer = amazonEmployer,
    )

    val nonExistentEmployer = Employer(
      id = EntityId("b9c925c1-c0d3-460d-8142-f79e7c292fce"),
      name = "Non-Existent Employer",
      description = "Daydreaming Inc.",
      sector = "LOGISTICS",
      status = "KEY_PARTNER",
    )

    val nonExistentJob = Job(
      id = EntityId("035fa5bb-1523-4469-a2a6-c6cf0ac94173"),
      title = "Non-Existent Job",
      sector = "WAREHOUSING",
      industrySector = "LOGISTICS",
      numberOfVacancies = 2,
      sourcePrimary = "PEL",
      sourceSecondary = "",
      charityName = "",
      postcode = "LS12",
      salaryFrom = 11.93f,
      salaryTo = 15.90f,
      salaryPeriod = "PER_HOUR",
      additionalSalaryInformation = "",
      isPayingAtLeastNationalMinimumWage = false,
      workPattern = "FLEXIBLE_SHIFTS",
      hoursPerWeek = "FULL_TIME",
      contractType = "TEMPORARY",
      baseLocation = "WORKPLACE",
      essentialCriteria = "",
      desirableCriteria = "",
      description = """
        This is a daydreaming job :)
      """.trimIndent(),
      offenceExclusions = "NONE,DRIVING",
      isRollingOpportunity = false,
      closingDate = LocalDate.of(2024, JULY, 20),
      isOnlyForPrisonLeavers = true,
      startDate = LocalDate.of(2024, JULY, 20),
      howToApply = "",
      supportingDocumentationRequired = "CV,DISCLOSURE_LETTER",
      supportingDocumentationDetails = "",
      employer = nonExistentEmployer,
    )

    val expectedPrisonNumber = "A1234BC"
  }
}
