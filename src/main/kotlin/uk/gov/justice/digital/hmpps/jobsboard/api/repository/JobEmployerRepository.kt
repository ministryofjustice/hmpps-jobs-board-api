package uk.gov.justice.digital.hmpps.jobsboard.api.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.JobEmployer

@Repository
interface JobEmployerRepository : JpaRepository<JobEmployer, Long>, PagingAndSortingRepository<JobEmployer, Long>
