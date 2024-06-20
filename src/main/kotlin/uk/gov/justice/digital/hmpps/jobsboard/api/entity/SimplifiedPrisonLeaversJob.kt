package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.hateoas.server.core.Relation
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.ContractHours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.Hours
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.SalaryPeriod
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import java.time.Instant
import java.time.LocalDateTime
import kotlin.jvm.Transient

@Relation(collectionRelation = "jobs", itemRelation = "job")
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "simple_prison_leavers_job")
class SimplifiedPrisonLeaversJob(

  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "job_id", nullable = false)
  var id: Long?,

  @Column(name = "salary_period_name", nullable = false)
  @Enumerated(EnumType.STRING)
  var salaryPeriodName: SalaryPeriod?,

  @Column(name = "work_pattern_name", nullable = false)
  @Enumerated(EnumType.STRING)
  var workPatternName: ContractHours?,

  @Column(name = "hours_name", nullable = false)
  @Enumerated(EnumType.STRING)
  var hoursName: Hours?,

  @Column(name = "job_contract_name", nullable = false)
  var jobContractName: String?,

  @Column(name = "job_type_name", nullable = true)
  var jobTypeName: String?,

  @Column(name = "job_contract_id", nullable = true)
  var jobContractId: Long?,

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "employer_id", nullable = false , insertable = false, updatable = false)
  var employer: SimplifiedJobEmployer?,

  @Column(name = "mn_sector_name", nullable = true)
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

  @Column(name = "mn_created_by_id", nullable = true)
  var mnCreatedById: Long?,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: Instant?,

  @Column(name = "posting_date")
  var postingDate: String?,

  @Column(name = "mn_deleted_by_id", nullable = false)
  var mnDeletedById: Long?,

  @Column(name = "deleted_by")
  var deletedBy: String?,

  @Column(name = "deleted_date_time")
  var deletedDateTime: Instant?,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: Instant?,

  @Column(name = "national_minimum_wage")
  var nationalMinimumWage: Boolean?,

  @Column(name = "post_code")
  var postCode: String?,

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
  @Enumerated(EnumType.STRING)
  var typeOfWork: TypeOfWork?,

  @Transient
  var distance: Long,
)
