package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import jakarta.persistence.CascadeType
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
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.TypeOfWork
import java.time.Instant
import kotlin.jvm.Transient

@Relation(collectionRelation = "jobs", itemRelation = "job")
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "prison_leavers_job")
class PrisonLeaversJob(

  @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "job_id", nullable = false)
  var id: Long?,

  @Column(name = "mn_job_id", nullable = false)
  var mnJobId: Long?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "salary_period_id", nullable = false)
  var salaryPeriod: SalaryPeriod?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "work_pattern_id", nullable = false)
  var workPattern: WorkPattern?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "hours_type_id", nullable = false)
  var hoursType: HoursType?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_contract_type_id", nullable = false)
  var jobContractType: JobContractType?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "job_type_id", nullable = false)
  var jobType: JobType?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "base_location_id", nullable = false)
  var baseLocation: BaseLocation?,

  @Column(name = "job_contract_id", nullable = false)
  var jobContractId: Long?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "employer_id", nullable = false)
  var employer: JobEmployer?,

  @ManyToOne(cascade = [CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "employer_sector_id", nullable = false)
  var employerSector: EmployerWorkSector?,

  @Column(name = "additional_salary_information")
  var additionalSalaryInformation: String?,

  @Column(name = "desirable_job_criteria")
  var desirableJobCriteria: String?,

  @Column(name = "essential_job_criteria")
  var essentialJobCriteria: String?,

  @Column(name = "closing_date")
  var closingDate: String?,

  @Column(name = "how_to_apply")
  var howToApply: String?,

  @Column(name = "job_title")
  var jobTitle: String?,

  @Column(name = "mn_created_by_id", nullable = false)
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
