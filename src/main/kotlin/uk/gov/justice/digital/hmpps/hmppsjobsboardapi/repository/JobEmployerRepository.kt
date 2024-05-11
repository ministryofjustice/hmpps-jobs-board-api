package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobEmployer
@Repository
interface JobEmployerRepository : JpaRepository<JobEmployer, Long>
