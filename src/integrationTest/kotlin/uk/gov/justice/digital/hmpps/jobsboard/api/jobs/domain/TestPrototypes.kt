package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.time.Instant
import java.time.LocalDate
import java.time.Month.JULY

class TestPrototypes {
  companion object {
    val defaultCreationTime: Instant = Instant.parse("2024-01-01T00:00:00Z")
    val employerCreationTime = Instant.parse("2024-07-01T01:00:00Z")
    val employerModificationTime = Instant.parse("2024-07-01T02:00:00Z")
    val jobCreationTime: Instant = Instant.parse("2024-01-01T00:00:00Z")
    val jobModificationTime = Instant.parse("2025-02-02T01:00:00Z")
    val jobRegisterExpressionOfInterestTime = Instant.parse("2025-03-01T01:00:00Z")
    val userTestName = "user-test-name"
    val anotherUserTestName = "another-user-test-name"
    val longUsername = "${"A".repeat(234)}@a.com"

    private val nonExistentEmployer = Employer(
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

    const val VALID_PRISON_NUMBER = "A1234BC"
  }
}
