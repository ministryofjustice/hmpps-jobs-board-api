package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

@ConsistentCopyVisibility
data class DeleteExpressionOfInterestRequest internal constructor(
  val jobId: String,
  val prisonNumber: String,
) {
  companion object {
    fun from(
      jobId: String,
      prisonNumber: String,
    ): DeleteExpressionOfInterestRequest = DeleteExpressionOfInterestRequest(
      jobId,
      prisonNumber,
    )
  }
}
