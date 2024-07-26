package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.jobsboard.api.employers.domain.Employer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import java.time.LocalDate

@Entity
@Table(name = "jobs")
data class Job(
  @Id
  var id: EntityId,

  @Column(name = "title", nullable = false)
  val title: String,

  @Column(name = "sector", nullable = false)
  val sector: String,

  @Column(name = "industry_sector", nullable = false)
  val industrySector: String,

  @Column(name = "number_of_vacancies", nullable = false)
  val numberOfVacancies: String,

  @Column(name = "source_primary", nullable = false)
  val sourcePrimary: String,

  @Column(name = "source_secondary", nullable = true)
  val sourceSecondary: String? = null,

  @Column(name = "charity_name", nullable = true)
  val charityName: String? = null,

  @Column(name = "post_code", nullable = false)
  val postCode: String,

  @Column(name = "salary_from", nullable = false)
  val salaryFrom: String,

  @Column(name = "salary_to", nullable = true)
  val salaryTo: String? = null,

  @Column(name = "salary_period", nullable = false)
  val salaryPeriod: String,

  @Column(name = "additional_salary_information", nullable = true)
  val additionalSalaryInformation: String? = null,

  @Column(name = "is_paying_at_least_national_minimum_wage", nullable = false)
  val isPayingAtLeastNationalMinimumWage: Boolean,

  @Column(name = "work_pattern", nullable = false)
  val workPattern: String,

  @Column(name = "hours_per_week", nullable = false)
  val hoursPerWeek: String,

  @Column(name = "contract_type", nullable = false)
  val contractType: String,

  @Column(name = "base_location", nullable = false)
  val baseLocation: String,

  @Column(name = "essential_criteria", nullable = false)
  val essentialCriteria: String,

  @Column(name = "desirable_criteria", nullable = true)
  val desirableCriteria: String? = null,

  @Column(name = "description", nullable = false)
  val description: String,

  @Column(name = "offence_exclusions", nullable = false)
  val offenceExclusions: String,

  @Column(name = "is_rolling_opportunity", nullable = false)
  val isRollingOpportunity: Boolean,

  @Column(name = "closing_date", nullable = true)
  val closingDate: LocalDate? = null,

  @Column(name = "is_only_for_prison_leavers", nullable = false)
  val isOnlyForPrisonLeavers: Boolean,

  @Column(name = "start_date", nullable = true)
  val startDate: LocalDate? = null,

  @Column(name = "how_to_apply", nullable = false)
  val howToApply: String,

  @Column(name = "supporting_documentation_required", nullable = false)
  val supportingDocumentationRequired: String,

  @Column(name = "supporting_documentation_details", nullable = true)
  val supportingDocumentationDetails: String? = null,

  @ManyToOne
  @JoinColumn(name = "employer_id", referencedColumnName = "id")
  val employer: Employer,
)
