package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobEvent
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobEventType
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEvent
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class JobCreator(
  private val jobRepository: JobRepository,
  private val employerRepository: EmployerRepository,
  private val postcodeLocationService: PostcodeLocationService,
  @param:Autowired(required = false)
  private val outboundEventsService: OutboundEventsService?,
  private val uuidGenerator: UUIDGenerator,
  private val timeProvider: TimeProvider,
) {
  private companion object {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun create(request: CreateJobRequest) {
    save(request)
      .also { sendIntegrationEvent(request.id, JobEventType.JOB_CREATED) }
  }

  fun update(request: CreateJobRequest) {
    val job = jobRepository.findById(EntityId(request.id))
      .orElseThrow { IllegalArgumentException("Job not found: id = ${request.id}") }
    save(request, job)
      .also { sendIntegrationEvent(request.id, JobEventType.JOB_UPDATED) }
  }

  private fun save(
    request: CreateJobRequest,
    originalJob: Job? = null,
  ) {
    val employer = employerRepository.findById(EntityId(request.employerId))
      .orElseThrow { IllegalArgumentException("Employer not found: employerId = ${request.employerId}") }

    // Validate postcode is present if the job is not national
    if (!request.isNational && request.postCode.isNullOrBlank()) {
      throw IllegalArgumentException("Postcode is required for non-national jobs")
    }

    if (!request.postCode.isNullOrBlank()) {
      postcodeLocationService.save(request.postCode)
    }

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
        isNational = request.isNational,
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

  fun existsById(jobId: String): Boolean = jobRepository.existsById(EntityId(jobId))

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

  private fun sendIntegrationEvent(jobId: String, eventType: JobEventType) {
    outboundEventsService?.let { service ->
      try {
        makeEventForJob(jobId, eventType).toOutboundEvent().let { event ->
          service.handleMessage(event)
        }
      } catch (e: Exception) {
        log.error("Fail to send integration event: jobId=$jobId; eventType=$eventType", e)
      }
    }
  }

  private fun makeEventForJob(
    jobId: String,
    jobEventType: JobEventType,
  ): JobEvent = JobEvent(
    eventId = uuidGenerator.generate(),
    eventType = jobEventType,
    timestamp = timeProvider.nowAsInstant(),
    jobId = jobId,
  )

  private fun JobEvent.toOutboundEvent(): OutboundEvent = OutboundEvent(
    eventId = eventId,
    eventType = eventType.type,
    timestamp = timestamp,
    content = """
       {
      "eventId": "$eventId",
      "eventType": "${eventType.eventTypeCode}",
      "timestamp": "$timestamp",
      "jobId": "$jobId"
      }
    """.trimIndent(),
  )
}
