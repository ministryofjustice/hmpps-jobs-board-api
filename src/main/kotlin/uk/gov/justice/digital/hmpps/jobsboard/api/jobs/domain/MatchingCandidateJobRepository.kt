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
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.MatchingCandidateJobsDTO
import java.time.LocalDate

const val CALC_DISTANCE_EXPRESSION = "CAST(ROUND(SQRT(POWER(pos2.xCoordinate - pos1.xCoordinate, 2) + POWER(pos2.yCoordinate - pos1.yCoordinate, 2)) / 1609.34, 1) AS FLOAT)"
const val CALC_DISTANCE_EXPRESSION_NATIVE = "CAST(ROUND(CAST(SQRT(POWER(pos2.x_coordinate - pos1.x_coordinate, 2) + POWER(pos2.y_coordinate - pos1.y_coordinate, 2)) / 1609.34 AS NUMERIC), 1) as REAL)"

@Repository
interface MatchingCandidateJobRepository : JpaRepository<Job, EntityId> {

  @Deprecated(level = DeprecationLevel.ERROR, message = "This query has been deprecated.", replaceWith = ReplaceWith("findAllJobs(prisonNumber, sectors, releaseArea, searchRadius, currentDate, isNationalJob, employerId, pageable)"))
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
      CASE WHEN pos1.xCoordinate IS NULL OR pos1.yCoordinate IS NULL
          THEN NULL
          WHEN pos2.xCoordinate IS NULL OR pos2.yCoordinate IS NULL
          THEN NULL
          ELSE CAST(
                 ROUND(
                   SQRT(
                     POWER(pos2.xCoordinate - pos1.xCoordinate, 2) +
                     POWER(pos2.yCoordinate - pos1.yCoordinate, 2)
                   ) / 1609.34,
                   1
                 ) AS float
               )
      END,
      j.isNational,
      j.numberOfVacancies
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
    AND (
      :isNationalJob IS TRUE      
      OR COALESCE(:searchRadius, 0) <= 0
      OR (pos1.xCoordinate IS NOT NULL AND pos1.yCoordinate IS NOT NULL AND COALESCE(CAST(ROUND(SQRT(POWER(pos2.xCoordinate - pos1.xCoordinate, 2) + POWER(pos2.yCoordinate - pos1.yCoordinate, 2)) / 1609.34, 1) AS FLOAT), -1) <= :searchRadius)
    )
    AND (j.isNational = :isNationalJob OR :isNationalJob IS NULL)
    AND (e.id.id = :employerId OR :employerId IS NULL)
  """,
  )
  fun findAll(
    @Param("prisonNumber") prisonNumber: String,
    @Param("sectors") sectors: List<String>?,
    @Param("releaseArea") releaseArea: String? = null,
    @Param("searchRadius") searchRadius: Int? = null,
    @Param("currentDate") currentDate: LocalDate,
    @Param("isNationalJob") isNationalJob: Boolean? = null,
    @Param("employerId") employerId: String? = null,
    pageable: Pageable,
  ): Page<GetMatchingCandidateJobsResponse>

  /**
   * Find all jobs
   *
   * notes: This trick will break if `sectors` or `offenceExclusions` can contain an empty string ""
   */
  fun findAllJobs(
    prisonNumber: String,
    sectors: List<String>? = null,
    releaseArea: String? = null,
    searchRadius: Int? = null,
    currentDate: LocalDate,
    isNationalJob: Boolean? = null,
    employerId: String? = null,
    offenceExclusions: List<String>? = null,
    pageable: Pageable,
  ) = findAllJobs(
    prisonNumber = prisonNumber,
    sectors = sectors.nonEmptyList(),
    applySectorsFilter = !sectors.isNullOrEmpty(),
    releaseArea = releaseArea,
    searchRadius = searchRadius,
    currentDate = currentDate,
    isNationalJob = isNationalJob,
    employerId = employerId,
    offenceExclusions = offenceExclusions.nonEmptyList(),
    applyOffenceExclusionsFilter = !offenceExclusions.isNullOrEmpty(),
    pageable = pageable,
  )

  // notes: This trick will break if the nullable list can contain an empty string ""
  private fun List<String>?.nonEmptyList(): List<String> = if (this.isNullOrEmpty()) listOf("") else this

  @Query(
    """
    SELECT 
      j.id,
      j.title as jobTitle,
      e.name as employerName,
      j.sector,
      j.postcode,
      j.closing_date as closingDate,
      CASE WHEN eoi.created_at IS NOT NULL THEN true ELSE false END as hasExpressedInterest,
      j.created_at as createdAt,
      CAST(ROUND(CAST(SQRT(POWER(pos2.x_coordinate - pos1.x_coordinate, 2) + POWER(pos2.y_coordinate - pos1.y_coordinate, 2)) / 1609.34 AS NUMERIC), 1) as REAL) as distance,
      j.is_national as isNational,
      j.number_of_vacancies as numberOfVacancies
    FROM jobs j
    LEFT JOIN jobs_expressions_of_interest eoi ON eoi.job_id = j.id AND eoi.prison_number = :prisonNumber
    LEFT JOIN employers e ON j.employer_id = e.id
    LEFT JOIN jobs_archived a ON a.job_id = j.id AND a.prison_number = :prisonNumber
    LEFT JOIN postcodes pos1 ON j.postcode = pos1.code
    LEFT JOIN postcodes pos2 ON pos2.code = :releaseArea
    WHERE  (j.closing_date >= :currentDate OR j.closing_date IS NULL)
    AND (NOT :applySectorsFilter OR LOWER(j.sector) IN :sectors)
    AND a.job_id IS NULL
    AND (
      :isNationalJob IS TRUE      
      OR COALESCE(:searchRadius, 0) <= 0
      OR (pos1.x_coordinate IS NOT NULL AND pos1.y_coordinate IS NOT NULL AND COALESCE(ROUND(CAST(SQRT(POWER(pos2.x_coordinate - pos1.x_coordinate, 2) + POWER(pos2.y_coordinate - pos1.y_coordinate, 2)) / 1609.34 AS NUMERIC), 1), -1) <= :searchRadius)
    )
    AND (j.is_national = :isNationalJob OR :isNationalJob IS NULL)
    AND (e.id = :employerId OR :employerId IS NULL)
    AND (
      NOT :applyOffenceExclusionsFilter OR 
      j.offence_exclusions = 'NONE' OR
      NOT EXISTS (SELECT 1 FROM unnest(string_to_array(j.offence_exclusions, ',')) oe WHERE oe IN (:offenceExclusions))
    )
    """,
    nativeQuery = true,
  )
  fun findAllJobs(
    @Param("prisonNumber") prisonNumber: String,
    @Param("sectors") sectors: List<String> = listOf(""),
    @Param("applySectorsFilter") applySectorsFilter: Boolean = false,
    @Param("releaseArea") releaseArea: String? = null,
    @Param("searchRadius") searchRadius: Int? = null,
    @Param("currentDate") currentDate: LocalDate,
    @Param("isNationalJob") isNationalJob: Boolean? = null,
    @Param("employerId") employerId: String? = null,
    @Param("offenceExclusions") offenceExclusions: List<String> = listOf(""),
    @Param("applyOffenceExclusionsFilter") applyOffenceExclusionsFilter: Boolean = false,
    pageable: Pageable,
  ): Page<MatchingCandidateJobsDTO>

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
      j.createdAt,
      j.isNational
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
      j.createdAt,
      j.isNational
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
      $CALC_DISTANCE_EXPRESSION,
      j.isNational,
      j.numberOfVacancies
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
      $CALC_DISTANCE_EXPRESSION,
      j.isNational,
      j.numberOfVacancies
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
