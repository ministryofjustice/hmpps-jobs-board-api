package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import io.swagger.v3.oas.annotations.Hidden
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.ArchivedCreator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.CreateArchivedRequest

@Validated
@RestController
@RequestMapping("/jobs/{jobId}/archived", produces = [APPLICATION_JSON_VALUE])
class ArchivedPut(
  private val archivedCreator: ArchivedCreator,
) {
  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @PutMapping("/{prisonNumber}")
  fun create(
    @PathVariable
    @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format",
    )
    jobId: String,
    @PathVariable
    @Size(max = 7, min = 1)
    prisonNumber: String,
  ): ResponseEntity<Void> {
    return if (archivedCreator.existsById(jobId, prisonNumber)) {
      ResponseEntity.ok().build()
    } else {
      archivedCreator.createOrUpdate(
        CreateArchivedRequest(jobId, prisonNumber),
      )
      ResponseEntity.created(
        ServletUriComponentsBuilder
          .fromCurrentRequest()
          .build()
          .toUri(),
      ).build()
    }
  }

  @Hidden
  @PutMapping("/")
  fun notFound(): ResponseEntity<Void> = ResponseEntity.notFound().build()
}