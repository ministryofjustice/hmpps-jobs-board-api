package uk.gov.justice.digital.hmpps.jobsboard.api.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.JobsBoardProfile

@Repository
interface JobsBoardProfileRepository :
  JpaRepository<JobsBoardProfile, String>,
  CustomRepository {

//  fun findById(offenderId: String): JobsBoardProfile?
}
