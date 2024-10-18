package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapKey
import jakarta.persistence.OneToMany
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
  val numberOfVacancies: Int,

  @Column(name = "source_primary", nullable = false)
  val sourcePrimary: String,

  @Column(name = "source_secondary", nullable = true)
  val sourceSecondary: String? = null,

  @Column(name = "charity_name", nullable = true)
  val charityName: String? = null,

  @Column(name = "post_code", nullable = false)
  val postcode: String,

  @Column(name = "salary_from", nullable = false)
  val salaryFrom: Float,

  @Column(name = "salary_to", nullable = true)
  val salaryTo: Float? = null,

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

  @Column(name = "base_location", nullable = true)
  val baseLocation: String? = null,

  @Column(name = "essential_criteria", nullable = false)
  val essentialCriteria: String,

  @Column(name = "desirable_criteria", nullable = true)
  val desirableCriteria: String? = null,

  @Column(name = "description", length = 3000, nullable = false)
  val description: String,

  @Column(name = "offence_exclusions", nullable = false)
  val offenceExclusions: String,

  @Column(name = "offence_exclusions_details", nullable = true)
  val offenceExclusionsDetails: String? = null,

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

  @Column(name = "supporting_documentation_required", nullable = true)
  val supportingDocumentationRequired: String? = null,

  @Column(name = "supporting_documentation_details", nullable = true)
  val supportingDocumentationDetails: String? = null,

  @JoinColumn(name = "employer_id", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  val employer: Employer,

  @OneToMany(mappedBy = "job", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
  @MapKey(name = "id.prisonNumber")
  val expressionsOfInterest: MutableMap<String, ExpressionOfInterest> = mutableMapOf(),

  @OneToMany(mappedBy = "job", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
  @MapKey(name = "id.prisonNumber")
  val archived: MutableMap<String, Archived> = mutableMapOf(),
) : Auditable() {
  override fun toString(): String = """
    Job(
        id=$id,
        title=$title,
        sector=$sector,
        industrySector=$industrySector,
        numberOfVacancies=$numberOfVacancies,
        sourcePrimary=$sourcePrimary,
        sourceSecondary=$sourceSecondary,
        charityName=$charityName,
        postcode=$postcode,
        salaryFrom=$salaryFrom,
        salaryTo=$salaryTo,
        salaryPeriod=$salaryPeriod,
        additionalSalaryInformation=$additionalSalaryInformation,
        isPayingAtLeastNationalMinimumWage=$isPayingAtLeastNationalMinimumWage,
        workPattern=$workPattern,
        hoursPerWeek=$hoursPerWeek,
        contractType=$contractType,
        baseLocation=$baseLocation,
        essentialCriteria=$essentialCriteria,
        desirableCriteria=$desirableCriteria,
        description=$description,
        offenceExclusions=$offenceExclusions,
        offenceExclusionsDetails=$offenceExclusionsDetails,
        isRollingOpportunity=$isRollingOpportunity,
        closingDate=$closingDate,
        isOnlyForPrisonLeavers=$isOnlyForPrisonLeavers,
        startDate=$startDate,
        howToApply=$howToApply,
        supportingDocumentationRequired=$supportingDocumentationRequired,
        supportingDocumentationDetails=$supportingDocumentationDetails,
        employer=$employer,
        expressionsOfInterest=$expressionsOfInterest,
        archived=$archived,
        createdBy=$createdBy,
        createdAt=$createdAt,
        lastModifiedBy=$lastModifiedBy,
        lastModifiedAt=$lastModifiedAt,
    )
  """.trimIndent()
}
