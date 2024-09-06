package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

@ConsistentCopyVisibility
data class CreateOrDeleteExpressionOfInterestRequest internal constructor(
  val jobId: String,
  val prisonNumber: String,
) {
  companion object {
    fun from(
      jobId: String,
      prisonNumber: String,
    ): CreateOrDeleteExpressionOfInterestRequest {
      return CreateOrDeleteExpressionOfInterestRequest(
        jobId,
        prisonNumber,
      )
    }
  }
}
