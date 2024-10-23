package uk.gov.justice.digital.hmpps.jobsboard.api.applications.application

@ConsistentCopyVisibility
data class CreateApplicationRequest internal constructor(
  val id: String = "",
  val jobId: String,
  val prisonNumber: String,
  val prisonId: String,
  val firstName: String?,
  val lastName: String?,
  val applicationStatus: String,
  val additionalInformation: String?,
) {
  companion object {
    fun from(
      id: String,
      jobId: String,
      prisonNumber: String,
      prisonId: String,
      firstName: String? = null,
      lastName: String? = null,
      applicationStatus: String,
      additionalInformation: String? = null,
    ) = CreateApplicationRequest(
      id = id,
      jobId = jobId,
      prisonNumber = prisonNumber,
      prisonId = prisonId,
      firstName = firstName,
      lastName = lastName,
      applicationStatus = applicationStatus,
      additionalInformation = additionalInformation,
    )
  }
}
