package uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain

enum class ApplicationStatus {
  APPLICATION_MADE,
  APPLICATION_UNSUCCESSFUL,
  SELECTED_FOR_INTERVIEW,
  INTERVIEW_BOOKED,
  UNSUCCESSFUL_AT_INTERVIEW,
  JOB_OFFER,
  ;

  companion object {
    val openStatus = listOf(APPLICATION_MADE, SELECTED_FOR_INTERVIEW, INTERVIEW_BOOKED)
    val closedStatus = listOf(APPLICATION_UNSUCCESSFUL, UNSUCCESSFUL_AT_INTERVIEW, JOB_OFFER)
  }
}
