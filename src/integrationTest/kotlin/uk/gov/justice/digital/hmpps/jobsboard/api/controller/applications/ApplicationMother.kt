package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.asJson
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.time.Instant
import java.util.*

object ApplicationMother {
  val createdBy = "CCOLUMBUS_GEN"
  val lastModifiedBy = "JSMITH_GEN"

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

  val Application.requestBody get() = applicationRequestBody(this)

  private fun applicationRequestBody(application: Application): String {
    return application.let {
      """
      {
        ${applicationBodyCommonFields(it)}
      }
      """.trimIndent()
    }
  }

  private fun applicationBodyCommonFields(application: Application): String {
    return application.let {
      """
        "jobId": "${it.job.id}",
        "prisonNumber": "${it.prisonNumber}",
        "prisonId": "${it.prisonId}",
        "firstName": ${it.firstName?.asJson()},
        "lastName": ${it.lastName?.asJson()},
        "applicationStatus": "${it.status}",
        "additionalInformation": ${it.additionalInformation?.asJson()}
      """.trimIndent()
    }
  }
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

  var createdBy: String? = ApplicationMother.createdBy
  var createdAt: Instant? = Instant.now().minusSeconds(1)
  var lastModifiedBy: String? = ApplicationMother.lastModifiedBy
  var lastModifiedAt: Instant? = Instant.now()

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
    this.lastModifiedBy = application.lastModifiedBy
    this.lastModifiedAt = application.lastModifiedAt
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
    it.lastModifiedBy = this.lastModifiedBy
    it.lastModifiedAt = this.lastModifiedAt
  }
}
