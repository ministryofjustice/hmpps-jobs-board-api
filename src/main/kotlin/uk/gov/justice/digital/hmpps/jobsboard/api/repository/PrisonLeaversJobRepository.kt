package uk.gov.justice.digital.hmpps.jobsboard.api.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedPrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork

@Repository
interface PrisonLeaversJobRepository : JpaRepository<SimplifiedPrisonLeaversJob, Long>, PagingAndSortingRepository<SimplifiedPrisonLeaversJob, Long> {
  fun findPrisonLeaversJobsByEmployerPostCode(postCode: String, paging: Pageable): Page<SimplifiedPrisonLeaversJob>
  fun findPrisonLeaversJobsByTypeOfWork(typeofWork: TypeOfWork, paging: Pageable): Page<SimplifiedPrisonLeaversJob>
  fun findPrisonLeaversJobsByTypeOfWorkAndEmployerPostCode(typeofWork: TypeOfWork, postCode: String, paging: Pageable): Page<SimplifiedPrisonLeaversJob>
}
