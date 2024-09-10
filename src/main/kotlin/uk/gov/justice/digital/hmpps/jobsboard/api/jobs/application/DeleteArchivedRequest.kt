package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

@ConsistentCopyVisibility
data class DeleteArchivedRequest internal constructor(
  val jobId: String,
  val prisonNumber: String,
) {
  companion object {
    fun from(
      jobId: String,
      prisonNumber: String,
    ): DeleteArchivedRequest {
      return DeleteArchivedRequest(
        jobId,
        prisonNumber,
      )
    }
  }
}
