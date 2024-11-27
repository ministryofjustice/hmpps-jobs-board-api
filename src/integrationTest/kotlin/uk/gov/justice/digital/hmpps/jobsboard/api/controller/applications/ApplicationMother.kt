package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.Application
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.domain.ApplicationStatus
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.abcConstructionApprentice
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.JobMother.tescoWarehouseHandler
import uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs.asJson
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.TestPrototypes.Companion.jobCreationTime
import java.time.Instant
import java.util.*

object ApplicationMother {
  val username = "test-client"
  val createdBy = "CCOLUMBUS_GEN"
  val lastModifiedBy = "JSMITH_GEN"

  val prisonMDI = "MDI"
  val prisonABC = "ABC"
  val prisonXYZ = "XYZ"
  val prisonDSB = "DSB"

  val knownApplicant = Applicant(
    prisonNumber = "GC12HDK",
    prisonId = prisonMDI,
    firstName = "JOE",
    lastName = "BLOGGS",
  )

  val applicantA = Applicant.from("A1111AA", prisonABC, firstName = "One")
  val applicantB = Applicant.from("B2222BB", prisonABC, "Double", "One")
  val applicantC = Applicant.from("C3333CC", prisonABC, "Three", "Half")
  val applicantD = Applicant.from("D4444DD", prisonABC, lastName = "Three Half")
  val applicantE = Applicant.from("E5555EE", prisonABC, firstName = "Half Three")

  val applicantDP1 = Applicant.from("D9999SB", prisonDSB, firstName = "Dashboard1", lastName = "Person")
  val applicantDP2 = Applicant.from("D8888SB", prisonDSB, firstName = "Dashboard2", lastName = "Person")

  val applicationToTescoWarehouseHandler = Application(
    id = EntityId("0ed3d1f1-2d21-450a-8e73-e5fd5477695d"),
    prisonNumber = knownApplicant.prisonNumber,
    prisonId = knownApplicant.prisonId,
    firstName = knownApplicant.firstName,
    lastName = knownApplicant.lastName,
    status = "APPLICATION_MADE",
    job = tescoWarehouseHandler,
  )

  val applicationToAmazonForkliftOperator = Application(
    id = EntityId("0cc48d05-e022-4309-99da-61bd21eb215c"),
    prisonNumber = knownApplicant.prisonNumber,
    prisonId = knownApplicant.prisonId,
    firstName = knownApplicant.firstName,
    lastName = knownApplicant.lastName,
    status = "APPLICATION_MADE",
    job = amazonForkliftOperator,
  )

  val applicationToAbcConstructionApprentice = Application(
    id = EntityId("1b635092-048f-4c88-b673-103e5e702122"),
    prisonNumber = knownApplicant.prisonNumber,
    prisonId = knownApplicant.prisonId,
    firstName = knownApplicant.firstName,
    lastName = knownApplicant.lastName,
    status = "UNSUCCESSFUL_AT_INTERVIEW",
    job = abcConstructionApprentice,
  )

  val applicationsFromPrisonMDI = listOf(
    applicationToAmazonForkliftOperator,
    applicationToTescoWarehouseHandler,
    applicationToAbcConstructionApprentice,
  )

  val applicationsFromPrisonABC = listOf(
    makeApplication(applicantA, amazonForkliftOperator, ApplicationStatus.APPLICATION_MADE),
    makeApplication(applicantB, amazonForkliftOperator, ApplicationStatus.APPLICATION_UNSUCCESSFUL),
    makeApplication(applicantC, amazonForkliftOperator, ApplicationStatus.SELECTED_FOR_INTERVIEW),
    makeApplication(applicantD, amazonForkliftOperator, ApplicationStatus.INTERVIEW_BOOKED),
    makeApplication(applicantA, tescoWarehouseHandler, ApplicationStatus.JOB_OFFER),
    makeApplication(applicantB, tescoWarehouseHandler, ApplicationStatus.UNSUCCESSFUL_AT_INTERVIEW),
    makeApplication(applicantE, tescoWarehouseHandler, ApplicationStatus.APPLICATION_MADE),
    makeApplication(applicantA, abcConstructionApprentice, ApplicationStatus.APPLICATION_MADE),
    makeApplication(applicantB, abcConstructionApprentice, ApplicationStatus.APPLICATION_MADE),
  )

  val applicationsMap = mapOf(
    applicantA to listOf(applicationsFromPrisonABC[0], applicationsFromPrisonABC[4], applicationsFromPrisonABC[7]),
    applicantB to listOf(applicationsFromPrisonABC[1], applicationsFromPrisonABC[5], applicationsFromPrisonABC[8]),
    applicantC to listOf(applicationsFromPrisonABC[2]),
    applicantD to listOf(applicationsFromPrisonABC[3]),
    applicantE to listOf(applicationsFromPrisonABC[6]),
  )

  val applicationsFromPrisonXYZ = listOf<Application>()

  val applicationsFromPrisonDSB: List<Application> get() = listOf(
    makeApplication(applicantDP1, amazonForkliftOperator, ApplicationStatus.APPLICATION_MADE),
    makeApplication(applicantDP1, tescoWarehouseHandler, ApplicationStatus.APPLICATION_MADE),
    makeApplication(applicantDP2, tescoWarehouseHandler, ApplicationStatus.APPLICATION_MADE),
    makeApplication(applicantDP2, abcConstructionApprentice, ApplicationStatus.APPLICATION_MADE),
  )

  fun builder() = ApplicationBuilder()

  val Application.requestBody get() = applicationRequestBody(this)

  val Application.historyResponseBody get() = applicationHistoryResponse(this)

  private fun applicationRequestBody(application: Application): String {
    return application.let {
      """
      {
        ${applicationBodyCommonFields(it)}
      }
      """.trimIndent()
    }
  }

  private fun applicationHistoryResponse(application: Application): Any {
    return application.let {
      """
      {
        "id": "${it.id.id}",
        ${applicationBodyCommonFields(it)},
        "createdBy": "$username",
        "createdAt": "$jobCreationTime",
        "modifiedBy": "$username",
        "modifiedAt": "$jobCreationTime"
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

  fun makeApplication(
    applicant: Applicant,
    job: Job,
    status: ApplicationStatus,
    additionalInformation: String? = null,
  ) = Application(
    id = EntityId(UUID.randomUUID().toString()),
    prisonNumber = applicant.prisonNumber,
    prisonId = applicant.prisonId,
    firstName = applicant.firstName,
    lastName = applicant.lastName,
    status = status.toString(),
    additionalInformation = additionalInformation,
    job = job,
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

data class Applicant(
  val prisonNumber: String,
  val prisonId: String,
  val firstName: String?,
  val lastName: String?,
) {
  companion object {
    fun from(prisonNumber: String, prisonId: String, firstName: String? = null, lastName: String? = null) =
      Applicant(prisonNumber, prisonId, firstName, lastName)
  }
}
