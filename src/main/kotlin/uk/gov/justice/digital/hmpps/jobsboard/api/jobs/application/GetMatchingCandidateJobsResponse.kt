package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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
  val numberOfVacancies: Int,
) {
  constructor(dto: MatchingCandidateJobsDTO) : this(
    id = dto.id,
    jobTitle = dto.jobTitle,
    employerName = dto.employerName,
    sector = dto.sector,
    postcode = dto.postcode,
    closingDate = dto.closingDate,
    hasExpressedInterest = dto.hasExpressedInterest,
    createdAt = dto.createdAt?.atZone(ZoneId.systemDefault())?.toInstant(),
    distance = dto.distance,
    isNational = dto.isNational,
    numberOfVacancies = dto.numberOfVacancies,
  )
}
