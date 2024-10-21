package uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId

@Repository
interface ApplicationRepository : JpaRepository<Application, EntityId>, RevisionRepository<Application, EntityId, Long>
