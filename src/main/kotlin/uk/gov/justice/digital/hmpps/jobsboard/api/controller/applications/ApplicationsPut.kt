package uk.gov.justice.digital.hmpps.jobsboard.api.controller.applications

import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.ApplicationCreator
import uk.gov.justice.digital.hmpps.jobsboard.api.applications.application.CreateApplicationRequest

@Validated
@RestController
@RequestMapping("/applications", produces = [APPLICATION_JSON_VALUE])
class ApplicationsPut(
  val applicationCreator: ApplicationCreator,
) {
  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @PutMapping("/{id}")
  fun createOrUpdate(
    @PathVariable
    @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format",
    )
    id: String,
    @Valid @RequestBody createApplicationRequest: CreateApplicationRequest,
  ): ResponseEntity<Void> {
    val exists = applicationCreator.existsById(id)
    applicationCreator.createOrUpdate(createApplicationRequest.copy(id = id))
    return if (exists) {
      ResponseEntity.ok().build()
    } else {
      ResponseEntity.created(
        ServletUriComponentsBuilder
          .fromCurrentRequest()
          .build()
          .toUri(),
      ).build()
    }
  }
}
