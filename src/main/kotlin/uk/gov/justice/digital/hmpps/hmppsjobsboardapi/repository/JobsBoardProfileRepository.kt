package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobsBoardProfile

@Repository
interface JobsBoardProfileRepository : JpaRepository<JobsBoardProfile, String>, CustomRepository {

//  fun findById(offenderId: String): JobsBoardProfile?
}
