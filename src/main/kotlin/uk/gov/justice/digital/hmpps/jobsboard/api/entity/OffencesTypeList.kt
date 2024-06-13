package uk.gov.justice.digital.hmpps.jobsboard.api.entity

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
@Entity
@Table(name = "offences_type_list")
class OffencesTypeList(
  @EmbeddedId
  @AttributeOverrides(
    *arrayOf(
      AttributeOverride(
        name = "offencesTypeListId",
        column = Column(name = "offences_type_list_id"),
      ),
      AttributeOverride(
        name = "OffencesType",
        column = Column(name = "offences_id"),
      ),
      AttributeOverride(
        name = "offencesOther",
        column = Column(name = "offences_other"),
      ),
    ),
  )
  var id: OffencesTypeListId?,

)
