package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.util.*

@Repository
interface PostcodesRepository : JpaRepository<Postcode, EntityId> {
  fun findByCode(code: String): Postcode?
}
