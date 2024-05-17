package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.config.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobEmployerDTO
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.enums.PrisonLeaversJobSort
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.service.PrisonLeaversJobService

@Validated
@RestController
@RequestMapping("/prison-leavers-job", produces = [MediaType.APPLICATION_JSON_VALUE])
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
    requestDTO: PrisonLeaversJob,
  ): PrisonLeaversJob {
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
  ): PrisonLeaversJob? {
    return prisonLeaversJobService.getPrisonersLeaversJob(prisonerLeaversJobId)
  }

  @PreAuthorize("hasRole('WORK_READINESS_EDIT')")
  @GetMapping("/list")
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
    @RequestParam
    mode: PrisonLeaversJobSort,
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    pageNo: Int,
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    pageSize: Int,
  ): MutableList<PrisonLeaversJob>? {
    return prisonLeaversJobService.getPagingList(pageNo, pageSize, mode)
  }
}
