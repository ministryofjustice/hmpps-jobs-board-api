package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.Instant

@Relation(collectionRelation = "jobs", itemRelation = "job")
@JsonInclude(Include.NON_NULL)
class PrisonLeaversJob(
  var id: Long?,

  var mnJobId: Long?,

  var salaryPeriod: SalaryPeriod?,

  var workPattern: WorkPattern?,

  var hoursType: HoursType?,

  var jobContractType: JobContractType?,

  var offencesTypeList: OffencesTypeList?,

  var jobSourceList: JobSourceList?,

  var jobSource1: JobSource?,

  var jobType: JobType?,

  var baseLocation: BaseLocation?,

  var charity: JobCharity?,

  var jobContractId: Long?,

  var employer: JobEmployer?,

  var employerSector: EmployerWorkSector?,

  var additionalSalaryInformation: String?,

  var desirableJobCriteria: String?,

  var essentialJobCriteria: String?,

  var closingDate: String?,

  var howToApply: String?,

  var jobTitle: String?,

  var mnCreatedById: Long?,

  var createdBy: String?,

  var createdDateTime: Instant?,

  var postingDate: String?,

  var mnDeletedById: Long?,

  var deletedBy: String?,

  var deletedDateTime: Instant?,

  var modifiedBy: String?,

  var modifiedDateTime: Instant?,

  var nationalMinimumWage: Boolean?,

  var postCode: String?,

  var ringFencedJob: Boolean?,

  var rollingJobOppurtunity: Boolean?,

  var activeJob: Boolean?,

  var deletedJob: Boolean?,

  var salaryFrom: String?,

  var salaryTo: String?,
  initialLinks: MutableIterable<Link>,
) : RepresentationModel<PrisonLeaversJob>()
