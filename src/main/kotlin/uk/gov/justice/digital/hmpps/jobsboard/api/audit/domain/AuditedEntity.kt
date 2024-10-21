package uk.gov.justice.digital.hmpps.jobsboard.api.audit.domain

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.envers.Audited
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Audited
@MappedSuperclass
abstract class AuditedEntity(
  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false)
  var createdBy: String? = null,

  @LastModifiedBy
  @Column(name = "last_modified_by", nullable = false, updatable = true)
  var lastModifiedBy: String? = null,

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: Instant? = null,

  @LastModifiedDate
  @Column(name = "last_modified_at", nullable = false, updatable = true)
  var lastModifiedAt: Instant? = null,
)
