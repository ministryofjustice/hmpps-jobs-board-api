package uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain

import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.CreateApplicationRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.tescoWarehouseHandler
import java.time.Instant
import java.util.*

object ApplicationMother {
  object KnownApplicant {
    val prisonNumber = "GC12HDK"
    val prisonId = "MDI"
    val firstName = "JOE"
    val lastName = "BLOGGS"
  }

  val applicationToTescoWarehouseHandler = Application(
    id = EntityId("0ed3d1f1-2d21-450a-8e73-e5fd5477695d"),
    prisonNumber = KnownApplicant.prisonNumber,
    prisonId = KnownApplicant.prisonId,
    firstName = KnownApplicant.firstName,
    lastName = KnownApplicant.lastName,
    status = "APPLICATION_MADE",
    job = tescoWarehouseHandler,
  )

  val applicationToAmazonForkliftOperator = Application(
    id = EntityId("0cc48d05-e022-4309-99da-61bd21eb215c"),
    prisonNumber = KnownApplicant.prisonNumber,
    prisonId = KnownApplicant.prisonId,
    firstName = KnownApplicant.firstName,
    lastName = KnownApplicant.lastName,
    status = "APPLICATION_MADE",
    job = amazonForkliftOperator,
  )

  val applicationToAbcConstructionApprentice = Application(
    id = EntityId("1b635092-048f-4c88-b673-103e5e702122"),
    prisonNumber = KnownApplicant.prisonNumber,
    prisonId = KnownApplicant.prisonId,
    firstName = KnownApplicant.firstName,
    lastName = KnownApplicant.lastName,
    status = "UNSUCCESSFUL_AT_INTERVIEW",
    job = abcConstructionApprentice,
  )

  fun builder() = ApplicationBuilder()

  val Application.createApplicationRequest get() = createApplicationRequest(this)

  private fun createApplicationRequest(application: Application) = CreateApplicationRequest.from(
    id = application.id.id,
    jobId = application.job.id.id,
    prisonNumber = application.prisonNumber,
    prisonId = application.prisonId,
    firstName = application.firstName,
    lastName = application.lastName,
    applicationStatus = application.status,
  )
}

class ApplicationBuilder {
  var id: String = UUID.randomUUID().toString()
  var prisonNumber: String = "GC12HDK"
  var prisonId: String = "MDI"
  var firstName: String? = "JOE"
  var lastName: String? = "BLOGGS"
  var status: String = "APPLICATION_MADE"
  var additionalInformation: String? = "Some additional information"
  var job: Job = JobMother.builder().build()

  var createdBy: String? = "CCOLUMBUS_GEN"
  var createdAt: Instant? = Instant.now().minusSeconds(1)
  var modifiedBy: String? = "JSMITH_GEN"
  var modifiedAt: Instant? = Instant.now()

  fun from(application: Application) = this.apply {
    this.id = application.id.id
    this.prisonNumber = application.prisonNumber
    this.prisonId = application.prisonId
    this.firstName = application.firstName
    this.lastName = application.lastName
    this.status = application.status
    this.additionalInformation = application.additionalInformation
    this.job = application.job.copy()
    this.createdBy = application.createdBy
    this.createdAt = application.createdAt
    this.modifiedBy = application.lastModifiedBy
    this.modifiedAt = application.lastModifiedAt
  }

  fun build() = Application(
    id = EntityId(this.id),
    prisonNumber = this.prisonNumber,
    prisonId = this.prisonId,
    firstName = this.firstName,
    lastName = this.lastName,
    status = this.status,
    additionalInformation = this.additionalInformation,
    job = this.job,
  ).also {
    it.createdBy = this.createdBy
    it.createdAt = this.createdAt
    it.lastModifiedBy = this.modifiedBy
    it.lastModifiedAt = this.modifiedAt
  }
}
