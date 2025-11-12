package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.ArchivedDeleter
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.DeleteArchivedRequest

@Validated
@RestController
@RequestMapping("/jobs/{jobId}/archived", produces = [APPLICATION_JSON_VALUE])
class ArchivedDelete(
  private val archivedDeleter: ArchivedDeleter,
) {
  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT','ROLE_JOBS_BOARD__JOBS__ARCHIVED__RW')")
  @DeleteMapping("/{prisonNumber}")
  fun delete(
    @PathVariable
    @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format",
    )
    jobId: String,
    @PathVariable
    @Size(max = 7, min = 1)
    prisonNumber: String,
  ): ResponseEntity<Void> = if (archivedDeleter.existsById(jobId, prisonNumber)) {
    archivedDeleter.delete(
      DeleteArchivedRequest(
        jobId,
        prisonNumber,
      ),
    )
    ResponseEntity.noContent().build()
  } else {
    ResponseEntity.notFound().build()
  }

  @Hidden
  @DeleteMapping("/")
  fun notFound(): ResponseEntity<Void> = ResponseEntity.notFound().build()
}
