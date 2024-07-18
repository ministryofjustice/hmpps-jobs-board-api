package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.CreateJobRequest
import java.time.LocalDateTime
import kotlin.jvm.Transient

@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "jobs")
class Job(

  @Id
  @Column(name = "id", nullable = false)
  var id: EntityId?,

  @ManyToOne(cascade = [REMOVE], fetch = LAZY, optional = false)
  @JoinColumn(name = "employer_id", nullable = false, updatable = false)
  var employer: Employer?,

  @Column(name = "salary_period_name", nullable = false)
  @Enumerated(STRING)
  var salaryPeriodName: SalaryPeriod?,

  @Column(name = "work_pattern_name", nullable = false)
  @Enumerated(STRING)
  var workPatternName: ContractHours?,

  @Column(name = "hours_name", nullable = false)
  @Enumerated(STRING)
  var hoursName: Hours?,

  @Column(name = "sector_name", nullable = true)
  var sectorName: String?,

  @Column(name = "additional_salary_information")
  var additionalSalaryInformation: String?,

  @Column(name = "desirable_job_criteria")
  var desirableJobCriteria: String?,

  @Column(name = "essential_job_criteria")
  var essentialJobCriteria: String?,

  @Column(name = "closing_date")
  var closingDate: LocalDateTime?,

  @Column(name = "how_to_apply")
  var howToApply: String?,

  @Column(name = "job_title")
  var jobTitle: String?,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: LocalDateTime?,

  @Column(name = "posting_date")
  var postingDate: String?,

  @Column(name = "deleted_by")
  var deletedBy: String?,

  @Column(name = "deleted_date_time")
  var deletedDateTime: LocalDateTime?,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: LocalDateTime?,

  @Column(name = "national_minimum_wage")
  var nationalMinimumWage: Boolean?,

  @Column(name = "post_code")
  var postCode: String?,

  @Column(name = "city")
  val city: String?,

  @Column(name = "ring_fenced_job")
  var ringFencedJob: Boolean?,

  @Column(name = "rolling_job_oppurtunity")
  var rollingJobOppurtunity: Boolean?,

  @Column(name = "active_job")
  var activeJob: Boolean?,

  @Column(name = "deleted_job")
  var deletedJob: Boolean?,

  @Column(name = "salary_from")
  var salaryFrom: String?,

  @Column(name = "salary_to")
  var salaryTo: String?,

  @Column(name = "type_of_work")
  @Enumerated(STRING)
  var typeOfWork: TypeOfWork?,

  @Transient
  var distance: Long,
) {

  constructor(jobRequest: CreateJobRequest, Employer: Employer) : this(
    id = EntityId(jobRequest.id!!),
    employer = Employer,
    sectorName = jobRequest.sectorName,
    salaryPeriodName = jobRequest.salaryPeriodName,
    workPatternName = jobRequest.workPatternName,
    hoursName = jobRequest.hoursName,
    additionalSalaryInformation = jobRequest.additionalSalaryInformation,
    desirableJobCriteria = jobRequest.desirableJobCriteria,
    essentialJobCriteria = jobRequest.essentialJobCriteria,
    closingDate = jobRequest.closingDate,
    howToApply = jobRequest.howToApply,
    jobTitle = jobRequest.jobTitle,
    createdBy = jobRequest.createdBy,
    createdDateTime = jobRequest.createdDateTime,
    postingDate = jobRequest.postingDate,
    deletedBy = jobRequest.deletedBy,
    deletedDateTime = jobRequest.deletedDateTime,
    modifiedBy = jobRequest.modifiedBy,
    modifiedDateTime = jobRequest.modifiedDateTime,
    nationalMinimumWage = jobRequest.nationalMinimumWage,
    postCode = jobRequest.postCode,
    city = jobRequest.city,
    ringFencedJob = jobRequest.ringFencedJob,
    rollingJobOppurtunity = jobRequest.rollingJobOppurtunity,
    activeJob = jobRequest.activeJob,
    deletedJob = jobRequest.deletedJob,
    salaryFrom = jobRequest.salaryFrom,
    salaryTo = jobRequest.salaryTo,
    typeOfWork = jobRequest.typeOfWork,
    distance = 0,
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Job

    if (id != other.id) return false

    return true
  }

  override fun hashCode(): Int {
    return id?.hashCode() ?: 0
  }
}
