package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobsClosingSoonResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse
import java.time.LocalDate

const val CALC_DISTANCE_EXPRESSION = "CAST(ROUND(SQRT(POWER(pos2.xCoordinate - pos1.xCoordinate, 2) + POWER(pos2.yCoordinate - pos1.yCoordinate, 2)) / 1609.34, 1) AS FLOAT)"

@Repository
interface MatchingCandidateJobRepository : JpaRepository<Job, EntityId> {

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse(
      j.id.id,
      j.title,
      e.name,
      j.sector,
      j.postcode,
      j.closingDate,
      CASE WHEN eoi.createdAt IS NOT NULL THEN true ELSE false END,
      j.createdAt,
      CAST(ROUND(SQRT(POWER(pos2.xCoordinate - pos1.xCoordinate, 2) + POWER(pos2.yCoordinate - pos1.yCoordinate, 2)) / 1609.34, 1) AS FLOAT)
    )
    FROM Job j
    LEFT JOIN ExpressionOfInterest eoi ON eoi.job.id.id = j.id.id AND eoi.id.prisonNumber = :prisonNumber
    LEFT JOIN Employer e ON j.employer.id.id = e.id.id
    LEFT JOIN Archived a ON a.job.id.id = j.id.id AND a.id.prisonNumber = :prisonNumber
    LEFT JOIN Postcode pos1 ON j.postcode = pos1.code
    LEFT JOIN Postcode pos2 ON pos2.code = :releaseArea
    WHERE  (j.closingDate >= :currentDate OR j.closingDate IS NULL)
    AND (LOWER(j.sector) IN :#{#sectors} OR :#{#sectors} IS NULL )
    AND a.id IS NULL
    AND (CAST(ROUND(SQRT(POWER(pos2.xCoordinate - pos1.xCoordinate, 2) + POWER(pos2.yCoordinate - pos1.yCoordinate, 2)) / 1609.34, 1) AS FLOAT) <= :searchRadius
    OR pos1.code IS NULL OR pos2.code IS NULL OR pos2.xCoordinate IS NULL OR pos2.yCoordinate IS NULL OR :searchRadius IS NULL)
  """,
  )
  fun findAll(
    @Param("prisonNumber") prisonNumber: String,
    @Param("sectors") sectors: List<String>?,
    @Param("releaseArea") releaseArea: String? = null,
    @Param("searchRadius") searchRadius: Int? = null,
    @Param("currentDate") currentDate: LocalDate,
    pageable: Pageable,
  ): Page<GetMatchingCandidateJobsResponse>

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobResponse(
      j.id.id,
      j.employer.name,
      j.title,
      j.closingDate,
      j.startDate,
      j.postcode,
      null,
      j.sector,
      j.salaryFrom,
      j.salaryTo,
      j.salaryPeriod,
      j.additionalSalaryInformation,
      j.workPattern,
      j.hoursPerWeek,
      j.contractType,
      j.numberOfVacancies,
      j.charityName,
      j.isOnlyForPrisonLeavers,
      j.offenceExclusions,
      j.offenceExclusionsDetails, 
      j.essentialCriteria,
      j.desirableCriteria,
      j.description,
      j.howToApply,
      CASE WHEN eoi.id IS NOT NULL THEN true ELSE false END,
      CASE WHEN a.id IS NOT NULL THEN true ELSE false END,
      j.createdAt
    )
    FROM Job j
    LEFT JOIN j.expressionsOfInterest eoi on eoi.id.prisonNumber = :prisonNumber 
    LEFT JOIN j.archived a on a.id.prisonNumber = :prisonNumber
    WHERE j.id.id = :jobId
    """,
  )
  fun findJobDetailsByPrisonNumber(
    @Param("jobId") jobId: String,
    @Param("prisonNumber") prisonNumber: String,
  ): List<GetMatchingCandidateJobResponse>

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobResponse(
      j.id.id,
      j.employer.name,
      j.title,
      j.closingDate,
      j.startDate,
      j.postcode,
      CAST(ROUND(SQRT(POWER(pos2.xCoordinate - pos1.xCoordinate, 2) + POWER(pos2.yCoordinate - pos1.yCoordinate, 2)) / 1609.34, 1) AS FLOAT),
      j.sector,
      j.salaryFrom,
      j.salaryTo,
      j.salaryPeriod,
      j.additionalSalaryInformation,
      j.workPattern,
      j.hoursPerWeek,
      j.contractType,
      j.numberOfVacancies,
      j.charityName,
      j.isOnlyForPrisonLeavers,
      j.offenceExclusions,
      j.offenceExclusionsDetails, 
      j.essentialCriteria,
      j.desirableCriteria,
      j.description,
      j.howToApply,
      CASE WHEN eoi.id IS NOT NULL THEN true ELSE false END,
      CASE WHEN a.id IS NOT NULL THEN true ELSE false END,
      j.createdAt
    )
    FROM Job j
    LEFT JOIN j.expressionsOfInterest eoi on eoi.id.prisonNumber = :prisonNumber 
    LEFT JOIN j.archived a on a.id.prisonNumber = :prisonNumber
    LEFT JOIN Postcode pos1 on j.postcode = pos1.code
    LEFT JOIN Postcode pos2 on pos2.code = :releaseAreaPostcode
    WHERE j.id.id = :jobId
    """,
  )
  fun findJobDetailsByPrisonNumberAndReleaseAreaPostcode(
    @Param("jobId") jobId: String,
    @Param("prisonNumber") prisonNumber: String,
    @Param("releaseAreaPostcode") releaseAreaPostcode: String?,
  ): List<GetMatchingCandidateJobResponse>

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobsClosingSoonResponse(
      j.id.id,
      e.name,
      j.title,
      j.closingDate,
      j.sector, 
      j.createdAt
    )
    FROM Job j
    JOIN j.employer e
    LEFT JOIN j.expressionsOfInterest eoi ON eoi.id.prisonNumber = :prisonNumber
    LEFT JOIN j.archived a ON a.id.prisonNumber = :prisonNumber
    WHERE (j.closingDate >= :currentDate OR j.closingDate IS NULL)
    AND eoi.id IS NULL 
    AND a.id IS NULL
    AND (LOWER(j.sector) IN :sectors OR :sectors IS NULL)
    ORDER BY j.closingDate ASC NULLS LAST
  """,
  )
  fun findJobsClosingSoon(
    @Param("prisonNumber") prisonNumber: String,
    @Param("sectors") sectors: List<String>?,
    @Param("currentDate") currentDate: LocalDate,
    pageable: Pageable,
  ): List<GetJobsClosingSoonResponse>

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetJobsClosingSoonResponse(
      j.id.id,
      e.name,
      j.title,
      j.closingDate,
      j.sector, 
      j.createdAt
    )
    FROM Job j
    JOIN j.employer e
    LEFT JOIN j.expressionsOfInterest eoi ON eoi.id.prisonNumber = :prisonNumber
    LEFT JOIN j.archived a ON a.id.prisonNumber = :prisonNumber
    WHERE (j.closingDate >= :currentDate OR j.closingDate IS NULL)
    AND eoi.id IS NOT NULL 
    AND a.id IS NULL
    ORDER BY j.closingDate ASC NULLS LAST
  """,
  )
  fun findJobsOfInterestClosingSoon(
    @Param("prisonNumber") prisonNumber: String,
    @Param("currentDate") currentDate: LocalDate,
  ): List<GetJobsClosingSoonResponse>

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse(
      j.id.id,
      j.title,
      e.name,
      j.sector,
      j.postcode,
      j.closingDate,
      CASE WHEN eoi.createdAt IS NOT NULL THEN true ELSE false END,
      j.createdAt,
      $CALC_DISTANCE_EXPRESSION
    )
    FROM Job j
    JOIN j.expressionsOfInterest eoi ON eoi.id.prisonNumber = :prisonNumber
    JOIN j.employer e
    LEFT JOIN j.archived a ON a.id.prisonNumber = :prisonNumber
    LEFT JOIN Postcode pos1 ON j.postcode = pos1.code
    LEFT JOIN Postcode pos2 ON pos2.code = :releaseAreaPostcode
    WHERE (j.closingDate >= :currentDate OR j.closingDate IS NULL) 
    AND a.id IS NULL
  """,
  )
  fun findJobsOfInterest(
    @Param("prisonNumber") prisonNumber: String,
    @Param("releaseAreaPostcode") releaseAreaPostcode: String?,
    @Param("currentDate") currentDate: LocalDate,
    pageable: Pageable,
  ): Page<GetMatchingCandidateJobsResponse>

  @Query(
    """
    SELECT new uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.GetMatchingCandidateJobsResponse(
      j.id.id,
      j.title,
      e.name,
      j.sector,
      j.postcode,
      j.closingDate,
      CASE WHEN eoi.createdAt IS NOT NULL THEN true ELSE false END,
      j.createdAt,
      $CALC_DISTANCE_EXPRESSION
    )
    FROM Job j
    JOIN j.employer e
    JOIN j.archived a ON a.id.prisonNumber = :prisonNumber
    LEFT JOIN j.expressionsOfInterest eoi ON eoi.id.prisonNumber = :prisonNumber
    LEFT JOIN Postcode pos1 ON j.postcode = pos1.code
    LEFT JOIN Postcode pos2 ON pos2.code = :releaseAreaPostcode
    WHERE (j.closingDate >= :currentDate OR j.closingDate IS NULL)
  """,
  )
  fun findArchivedJobs(
    @Param("prisonNumber") prisonNumber: String,
    @Param("releaseAreaPostcode") releaseAreaPostcode: String?,
    @Param("currentDate") currentDate: LocalDate,
    pageable: Pageable,
  ): Page<GetMatchingCandidateJobsResponse>
}
