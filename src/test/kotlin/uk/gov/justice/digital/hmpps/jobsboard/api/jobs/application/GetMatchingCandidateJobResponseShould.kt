package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import kotlin.test.Test

class GetMatchingCandidateJobResponseShould : TestBase() {
  @Test
  fun `create a response from Job`() {
    val job = amazonForkliftOperator
    val actual = GetMatchingCandidateJobResponse(
      id = job.id.id,
      employerName = job.employer.name,
      jobTitle = job.title,
      closingDate = job.closingDate,
      startDate = job.startDate,
      postcode = job.postcode,
      isNational = job.isNational,
      distance = null,
      sector = job.sector,
      salaryFrom = job.salaryFrom,
      salaryTo = job.salaryTo,
      salaryPeriod = job.salaryPeriod,
      additionalSalaryInformation = job.additionalSalaryInformation,
      workPattern = job.workPattern,
      hoursPerWeek = job.hoursPerWeek,
      contractType = job.contractType,
      numberOfVacancies = job.numberOfVacancies,
      charityName = job.charityName,
      isOnlyForPrisonLeavers = job.isOnlyForPrisonLeavers,
      offenceExclusions = job.offenceExclusions,
      offenceExclusionsDetails = job.offenceExclusionsDetails,
      essentialCriteria = job.essentialCriteria,
      desirableCriteria = job.desirableCriteria,
      description = job.description,
      howToApply = job.howToApply,
      expressionOfInterest = false,
      archived = false,
      createdAt = job.createdAt,
    )

    assertMatchingCandidateJobResponse(actual, job, false, false)
  }

  private fun assertMatchingCandidateJobResponse(
    actual: GetMatchingCandidateJobResponse,
    expectedJob: Job,
    expectedExpressionOfInterest: Boolean? = null,
    expectedArchived: Boolean? = null,
    expectedDistance: Float? = null,
  ) {
    assertThat(actual).isNotNull.usingRecursiveComparison()
      .ignoringFields(
        "id",
        "jobTitle",
        "employerName",
        "closingDate",
        "startDate",
        "offenceExclusions",
        "createdAt",
        "distance",
        "expressionOfInterest",
        "archived",
      )
      .isEqualTo(expectedJob)
    assertThat(actual.id).isEqualTo(expectedJob.id.id)
    assertThat(actual.jobTitle).isEqualTo(expectedJob.title)
    assertThat(actual.employerName).isEqualTo(expectedJob.employer.name)
    assertThat(actual.closingDate).isEqualTo(expectedJob.closingDate?.toString())
    assertThat(actual.startDate).isEqualTo(expectedJob.startDate?.toString())
    assertThat(actual.offenceExclusions.joinToString(separator = ",")).isEqualTo(expectedJob.offenceExclusions)
    assertThat(actual.offenceExclusionsDetails).isEqualTo(expectedJob.offenceExclusionsDetails)
    assertThat(actual.createdAt).isEqualTo(expectedJob.createdAt.toString())

    expectedExpressionOfInterest?.let { assertThat(actual.expressionOfInterest).isEqualTo(it) }
    expectedArchived?.let { assertThat(actual.archived).isEqualTo(it) }
    expectedDistance?.let { assertThat(actual.distance).isEqualTo(it) }
  }
}
