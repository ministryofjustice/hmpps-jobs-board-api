package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpressionOfInterestRepository : JpaRepository<ExpressionOfInterest, JobPrisonerId> {
  fun findByIdPrisonNumber(prisonNumber: String): List<ExpressionOfInterest>
}
