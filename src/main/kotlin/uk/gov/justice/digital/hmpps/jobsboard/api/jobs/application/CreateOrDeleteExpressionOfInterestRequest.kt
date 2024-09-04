package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

@ConsistentCopyVisibility
data class CreateOrDeleteExpressionOfInterestRequest internal constructor(
  val jobId: String,
  val prisonerPrisonNumber: String,
) {
  companion object {
    fun from(
      jobId: String,
      prisonerPrisonNumber: String,
    ): CreateOrDeleteExpressionOfInterestRequest {
      return CreateOrDeleteExpressionOfInterestRequest(
        jobId,
        prisonerPrisonNumber,
      )
    }
  }
}
