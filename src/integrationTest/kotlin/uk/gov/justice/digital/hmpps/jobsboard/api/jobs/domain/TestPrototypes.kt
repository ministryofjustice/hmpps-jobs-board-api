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
