package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable {
  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  var createdBy: String? = null

  @LastModifiedBy
  @Column(name = "last_modified_by", nullable = false, updatable = false)
  var lastModifiedBy: String? = null

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: Instant? = null

  @LastModifiedDate
  @Column(name = "modified_at", nullable = false, updatable = true)
  var modifiedAt: Instant? = null
}
