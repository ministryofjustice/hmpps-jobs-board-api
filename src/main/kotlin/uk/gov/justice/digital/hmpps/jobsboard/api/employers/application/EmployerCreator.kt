package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerEvent
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerEventType
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.EmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.exceptions.NotFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.UUIDGenerator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.OutboundEventsService
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.domain.OutboundEvent
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider

@Service
class EmployerCreator(
  private val employerRepository: EmployerRepository,
  @Autowired(required = false)
  private val outboundEventsService: OutboundEventsService?,
  private val uuidGenerator: UUIDGenerator,
  private val timeProvider: TimeProvider,
) {
  private companion object {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun create(request: CreateEmployerRequest) {
    val jobs = emptyList<Job>()
    save(request, jobs)
      .also { sendIntegrationEvent(request.id, EmployerEventType.EMPLOYER_CREATED) }
  }

  fun update(request: CreateEmployerRequest) {
    val employer = employerRepository.findById(EntityId(request.id))
      .orElseThrow { NotFoundException("Employer not found: employerId = ${request.id}") }
    save(request, employer.jobs)
      .also { sendIntegrationEvent(request.id, EmployerEventType.EMPLOYER_UPDATED) }
  }

  fun existsById(employerId: String): Boolean = employerRepository.existsById(EntityId(employerId))

  fun validate(request: CreateEmployerRequest) {
    val duplicateCount = request.let { employerRepository.countByNameIgnoreCaseAndIdNot(it.name.trim(), EntityId(it.id)) }
    if (duplicateCount > 0) throw EmployerValidationException.duplicateEmployer(timeProvider.nowAsInstant())
  }

  private fun save(request: CreateEmployerRequest, jobs: List<Job>) {
    employerRepository.save(
      Employer(
        id = EntityId(request.id),
        name = request.name,
        description = request.description,
        sector = request.sector,
        status = request.status,
        jobs = jobs,
      ),
    )
  }

  private fun sendIntegrationEvent(employerId: String, eventType: EmployerEventType) {
    outboundEventsService?.let { service ->
      try {
        makeEventForEmployer(employerId, eventType).toOutboundEvent().let { event ->
          service.handleMessage(event)
        }
      } catch (e: Exception) {
        log.error("Fail to send integration event: employerId=$employerId; eventType=$eventType", e)
      }
    }
  }

  private fun makeEventForEmployer(
    employerId: String,
    employerEventType: EmployerEventType,
  ): EmployerEvent = EmployerEvent(
    eventId = uuidGenerator.generate(),
    eventType = employerEventType,
    timestamp = timeProvider.nowAsInstant(),
    employerId = employerId,
  )

  private fun EmployerEvent.toOutboundEvent(): OutboundEvent = OutboundEvent(
    eventId = eventId,
    eventType = eventType.type,
    timestamp = timestamp,
    content = """
       {
      "eventId": "$eventId",
      "eventType": "${eventType.eventTypeCode}",
      "timestamp": "$timestamp",
      "employerId": "$employerId"
      }
    """.trimIndent(),
  )
}
