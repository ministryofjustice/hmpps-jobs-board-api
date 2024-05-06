package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Embeddable
class OffencesTypeListId  (
  @Column(name = "offences_type_list_id", nullable = false)
  var offencesTypeListId: Long? ,

  @Column(name = "offences_id", nullable = false)
  var offencesId: Long? ,

  @Column(name = "offences_other", nullable = false)
  var offencesOther: String?
):Serializable{
  

 
}