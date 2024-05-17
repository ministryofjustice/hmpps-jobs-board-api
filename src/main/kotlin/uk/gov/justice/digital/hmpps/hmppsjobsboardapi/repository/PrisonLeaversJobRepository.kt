package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.PrisonLeaversJob

@Repository
interface PrisonLeaversJobRepository : JpaRepository<PrisonLeaversJob, Long>, PagingAndSortingRepository<PrisonLeaversJob, Long>
