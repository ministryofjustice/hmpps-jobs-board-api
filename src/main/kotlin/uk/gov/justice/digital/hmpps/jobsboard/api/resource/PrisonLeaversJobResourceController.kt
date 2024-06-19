package uk.gov.justice.digital.hmpps.jobsboard.api.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.JobEmployerDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedPrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobDetailDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobListPageDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversPagingDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.service.PrisonLeaversJobService

@Validated
@RestController
@RequestMapping("/candidate-matching/job", produces = [MediaType.APPLICATION_JSON_VALUE])
class PrisonLeaversJobResourceController(
  private val prisonLeaversJobService: PrisonLeaversJobService,
) {
  @PreAuthorize("hasRole('WORK_READINESS_EDIT')")
  @PostMapping
  @Operation(
    summary = "Create a job employer ",
    description = "Create a job employer. Currently requires role <b>ROLE_VIEW_PRISONER_DATA</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Job Employer  created",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = JobEmployerDTO::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Incorrect permissions to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun createPrisonLeaversJob(
    @Valid
    @RequestBody
    requestDTO: SimplifiedPrisonLeaversJob,
  ): SimplifiedPrisonLeaversJob {
    return prisonLeaversJobService.createJob(requestDTO)
  }

  @PreAuthorize("hasRole('WORK_READINESS_EDIT')")
  @GetMapping("/{prisonerLeaversJobId}")
  @Operation(
    summary = "Create a job employer ",
    description = "Create a job employer. Currently requires role <b>ROLE_VIEW_PRISONER_DATA</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Job Employer  created",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = JobEmployerDTO::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Incorrect permissions to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun getPrisonLeaversJob(
    @PathVariable
    prisonerLeaversJobId: Long,
  ): PrisonLeaversJobDetailDTO {
    return prisonLeaversJobService.getPrisonersLeaversJob(prisonerLeaversJobId)
  }

  @PreAuthorize("hasRole('WORK_READINESS_EDIT')")
  @PostMapping("/search")
  @Operation(
    summary = "Create a job employer ",
    description = "Create a job employer. Currently requires role <b>ROLE_VIEW_PRISONER_DATA</b>",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Job Employer  created",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = JobEmployerDTO::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Incorrect permissions to access this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun getPagedPrisonLeaversJobList(
    @Valid
    @RequestBody
    requestDTO: PrisonLeaversPagingDTO,
  ): PrisonLeaversJobListPageDTO {
    var plist = prisonLeaversJobService.getPagingList(requestDTO)
    return plist
  }
}
