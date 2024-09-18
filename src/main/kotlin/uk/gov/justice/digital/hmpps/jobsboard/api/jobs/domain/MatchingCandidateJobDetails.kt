package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

data class MatchingCandidateJobDetails(
  val job: Job,
  val prisonNumber: String? = null,
  val expressionOfInterest: ExpressionOfInterest? = null,
  val archived: Archived? = null,
) {
  fun hasExpressionOfInterest() = expressionOfInterest != null

  fun isArchived() = archived != null
}
