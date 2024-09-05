package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

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
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.CreateOrDeleteExpressionOfInterestRequest
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.ExpressionOfInterestEditor

@Validated
@RestController
@RequestMapping("/jobs/{jobId}/expressions-of-interest", produces = [APPLICATION_JSON_VALUE])
class ExpressionsOfInterestPut(
  private val expressionOfInterestCreator: ExpressionOfInterestEditor,
) {
  @PreAuthorize("hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @PutMapping("/{prisonerPrisonNumber}")
  fun create(
    @PathVariable
    @Pattern(
      regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
      message = "Invalid UUID format",
    )
    jobId: String,
    @PathVariable
    @Size(max = 7, min = 1)
    prisonerPrisonNumber: String,
  ): ResponseEntity<Void> {
    val created = expressionOfInterestCreator.createWhenNotExist(
      CreateOrDeleteExpressionOfInterestRequest(
        jobId,
        prisonerPrisonNumber,
      ),
    )

    return if (created) {
      ResponseEntity.created(
        ServletUriComponentsBuilder
          .fromCurrentRequest()
          .path("/{prisonerPrisonNumber}")
          .buildAndExpand(prisonerPrisonNumber)
          .toUri(),
      ).build()
    } else {
      ResponseEntity.ok().build()
    }
  }

  @PutMapping("/")
  fun notFound(): ResponseEntity<Void> = ResponseEntity.notFound().build()
}
