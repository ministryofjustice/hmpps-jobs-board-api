package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import java.time.Instant
import java.time.LocalDate

data class GetMatchingCandidateJobsResponse(
  val id: String,
  val jobTitle: String,
  val employerName: String,
  val sector: String,
  val postcode: String? = null,
  val closingDate: LocalDate? = null,
  val hasExpressedInterest: Boolean = false,
  val createdAt: Instant? = null,
  val distance: Float?,
  val isNational: Boolean = false,
)
