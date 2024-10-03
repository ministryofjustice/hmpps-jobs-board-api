package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.builder
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.candidateMatchingItemListResponseBody
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler

class MatchingCandidateGetShould : MatchingCandidateTestCase() {

  @Test
  fun `retrieve a default paginated empty matching candidate Jobs list`() {
    assertGetMatchingCandidateJobsIsOK(
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = expectedResponseListOf(),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOK(
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.candidateMatchingItemListResponseBody,
        amazonForkliftOperator.candidateMatchingItemListResponseBody,
        abcConstructionApprentice.candidateMatchingItemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list given a candidate expressed interest on a job`() {
    givenThreeJobsAreCreated()
    assertAddExpressionOfInterest(abcConstructionApprentice.id.id, prisonNumber)

    assertGetMatchingCandidateJobsIsOK(
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.candidateMatchingItemListResponseBody,
        amazonForkliftOperator.candidateMatchingItemListResponseBody,
        builder()
          .from(abcConstructionApprentice)
          .withExpressionOfInterestFrom(prisonNumber)
          .build().candidateMatchingItemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list given different candidates expressed interest on the same job`() {
    givenThreeJobsAreCreated()
    assertAddExpressionOfInterest(abcConstructionApprentice.id.id, prisonNumber)
    assertAddExpressionOfInterest(abcConstructionApprentice.id.id, anotherPrisonNumber)

    assertGetMatchingCandidateJobsIsOK(
      parameters = "prisonNumber=$prisonNumber",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.candidateMatchingItemListResponseBody,
        amazonForkliftOperator.candidateMatchingItemListResponseBody,
        builder()
          .from(abcConstructionApprentice)
          .withExpressionOfInterestFrom(prisonNumber)
          .build().candidateMatchingItemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a custom paginated matching candidate Jobs list`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOK(
      parameters = "prisonNumber=$prisonNumber&page=1&size=1",
      expectedResponse = expectedResponseListOf(
        size = 1,
        page = 1,
        totalElements = 3,
        amazonForkliftOperator.candidateMatchingItemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list filtered by job sector`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOK(
      parameters = "prisonNumber=$prisonNumber&sectors=retail",
      expectedResponse = expectedResponseListOf(
        amazonForkliftOperator.candidateMatchingItemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list filtered by job sectors`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOK(
      parameters = "prisonNumber=$prisonNumber&sectors=retail,warehousing",
      expectedResponse = expectedResponseListOf(
        tescoWarehouseHandler.candidateMatchingItemListResponseBody,
        amazonForkliftOperator.candidateMatchingItemListResponseBody,
      ),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list sorted by job title, in ascending order`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
      parameters = "prisonNumber=$prisonNumber&sortBy=jobTitle&sortOrder=asc",
      expectedJobTitlesSorted = listOf(
        "Apprentice plasterer",
        "Forklift operator",
        "Warehouse handler",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list sorted by job title, in descending order`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOKAndSortedByJobTitle(
      parameters = "prisonNumber=$prisonNumber&sortBy=jobTitle&sortOrder=desc",
      expectedJobTitlesSorted = listOf(
        "Warehouse handler",
        "Forklift operator",
        "Apprentice plasterer",
      ),
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list sorted by closing date, in ascending order, by default`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
      parameters = "prisonNumber=$prisonNumber&sortBy=closingDate",
      expectedSortingOrder = "asc",
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list sorted by closing date, in ascending order`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
      parameters = "prisonNumber=$prisonNumber&sortBy=closingDate&sortOrder=asc",
      expectedSortingOrder = "asc",
    )
  }

  @Test
  fun `retrieve a default paginated matching candidate Jobs list sorted by closing date, in descending order`() {
    givenThreeJobsAreCreated()

    assertGetMatchingCandidateJobsIsOKAndSortedByClosingDate(
      parameters = "prisonNumber=$prisonNumber&sortBy=closingDate&sortOrder=desc",
      expectedSortingOrder = "desc",
    )
  }
}
