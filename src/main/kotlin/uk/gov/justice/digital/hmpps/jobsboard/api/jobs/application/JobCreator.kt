package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class JobCreator(
  private val jobRepository: JobRepository,
  private val employerRepository: EmployerRepository,
  private val postcodeLocationService: PostcodeLocationService,
) {
  fun create(request: CreateJobRequest) {
    save(request)
  }

  fun update(request: CreateJobRequest) {
    val job = jobRepository.findById(EntityId(request.id))
      .orElseThrow { IllegalArgumentException("Job not found: id = ${request.id}") }
    save(request, job)
  }

  private fun save(
    request: CreateJobRequest,
    originalJob: Job? = null,
  ) {
    val employer = employerRepository.findById(EntityId(request.employerId))
      .orElseThrow { IllegalArgumentException("Employer not found: employerId = ${request.employerId}") }

    postcodeLocationService.save(request.postCode)

    jobRepository.save(
      Job(
        id = EntityId(request.id),
        title = request.jobTitle,
        sector = request.sector,
        industrySector = request.industrySector,
        numberOfVacancies = request.numberOfVacancies,
        sourcePrimary = request.sourcePrimary,
        sourceSecondary = request.sourceSecondary,
        charityName = request.charityName,
        postcode = request.postCode,
        salaryFrom = request.salaryFrom,
        salaryTo = request.salaryTo,
        salaryPeriod = request.salaryPeriod,
        additionalSalaryInformation = request.additionalSalaryInformation,
        isPayingAtLeastNationalMinimumWage = request.isPayingAtLeastNationalMinimumWage,
        workPattern = request.workPattern,
        hoursPerWeek = request.hoursPerWeek,
        contractType = request.contractType,
        baseLocation = request.baseLocation,
        essentialCriteria = request.essentialCriteria,
        desirableCriteria = request.desirableCriteria,
        description = request.description,
        offenceExclusions = request.offenceExclusions.joinToString(","),
        offenceExclusionsDetails = request.offenceExclusionsDetails,
        isRollingOpportunity = request.isRollingOpportunity,
        closingDate = request.closingDate?.toLocalDate(),
        isOnlyForPrisonLeavers = request.isOnlyForPrisonLeavers,
        startDate = request.startDate?.toLocalDate(),
        howToApply = request.howToApply,
        supportingDocumentationRequired = request.supportingDocumentationRequired?.joinToString(","),
        supportingDocumentationDetails = request.supportingDocumentationDetails,
        employer = employer,
        expressionsOfInterest = originalJob?.expressionsOfInterest ?: mutableMapOf(),
        archived = originalJob?.archived ?: mutableMapOf(),
        applications = originalJob?.applications ?: listOf(),
      ),
    )
  }

  fun existsById(jobId: String): Boolean {
    return jobRepository.existsById(EntityId(jobId))
  }

  private fun String.toLocalDate(): LocalDate? {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val zonedDateTime = try {
      LocalDate.parse(this, formatter)
    } catch (e: DateTimeParseException) {
      println("Error parsing date-time: ${e.message}")
      null
    }
    return zonedDateTime
  }
}
