package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ColumnResult
import jakarta.persistence.ConstructorResult
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.NamedNativeQuery
import jakarta.persistence.SqlResultSetMapping
import jakarta.persistence.Table
import org.springframework.hateoas.server.core.Relation
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PLIntrestedJobsClosingSoonDTO
import java.time.LocalDateTime

@NamedNativeQuery(
  name = "PrisonLeaversProfile.findIntrestedJobsClosingSoon",
  query = "select pljit.prison_leaver_id as prisonLeaverId,plj.job_id as jobId,plj.closing_date as closingDate,plj.job_title as jobTitle ,je.employer_name as employerName" +
    " from prison_leaver_job_intrest_table pljit " +
    "inner Join  simple_prison_leavers_job plj on pljit.prison_leavers_job_id=plj.job_id " +
    " inner join job_employers je on je.employer_id = plj.employer_id where  pljit.prison_leaver_id=:prisonLeaversId and plj.closing_date>= now() order by plj.closing_date asc ",
  resultSetMapping = "Mapping.PrisonLeaversSearchResultDTO",
)
@SqlResultSetMapping(
  name = "Mapping.PrisonLeaversSearchResultDTO",
  classes = [
    ConstructorResult(
      targetClass = PLIntrestedJobsClosingSoonDTO::class,
      columns = arrayOf(
        ColumnResult(name = "prisonLeaverId"),
        ColumnResult(name = "jobId"),
        ColumnResult(name = "employerName"),
        ColumnResult(name = "jobTitle"),
        ColumnResult(name = "typeOfWork", type = TypeOfWork::class),
        ColumnResult(name = "closingDate", type = LocalDateTime::class),
      ),
    ),
  ],
)
@Relation(collectionRelation = "jobs", itemRelation = "job")
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "prison_leavers_profile")
class PrisonLeaversProfile(

  @Id
  @Column(name = "prison_leaver_id", nullable = false)
  var id: String,

  @Column(name = "created_by")
  var createdBy: String?,

  @Column(name = "created_date_time")
  var createdDateTime: LocalDateTime?,

  @Column(name = "modified_by")
  var modifiedBy: String?,

  @Column(name = "modified_date_time")
  var modifiedDateTime: LocalDateTime?,

  @ManyToMany(cascade = [CascadeType.MERGE, CascadeType.REMOVE], fetch = FetchType.EAGER)
  @JoinTable(
    name = "prison_leaver_job_intrest_table",
    joinColumns = arrayOf(JoinColumn(name = "PRISON_LEAVER_ID")),
    inverseJoinColumns = arrayOf(JoinColumn(name = "PRISON_LEAVERS_JOB_ID")),
  )
  var jobs: MutableList<PrisonLeaversJob?>,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as PrisonLeaversProfile

    if (id != other.id) return false

    return true
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }
}
