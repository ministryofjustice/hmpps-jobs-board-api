package uk.gov.justice.digital.hmpps.jobsboard.api.resource

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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EmployerPartner
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EmployerPartnerGrade
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EmployerWorkSector
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.JobImage
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedJobEmployer
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedJobEmployerDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.JobEmployerDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.service.JobEmployerService
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping("/job-board/employer", produces = [MediaType.APPLICATION_JSON_VALUE])
class JobsEmployerResourceController(
  private val jobEmployerService: JobEmployerService,
) {
  @PreAuthorize("hasRole('WORK_READINESS_EDIT') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
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
  fun createEmployer(
    @Valid
    @RequestBody
    requestDTO: SimplifiedJobEmployerDTO,
  ): SimplifiedJobEmployerDTO {
    return jobEmployerService.createEmployer(requestDTO)
  }

  @PreAuthorize("hasRole('WORK_READINESS_EDIT') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/test")
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
  fun getEmployer(): JobEmployerDTO {
    return JobEmployerDTO(
      1L,
      "test",
      "asdad",
      "sacintha",
      LocalDateTime.now(),
      "test",
      LocalDateTime.now(),
      EmployerWorkSector(1L, 1L, "test", "test"),
      EmployerPartner(1L, EmployerPartnerGrade(1L, 1L, "test", "test"), 1L, "test", "test1"),
      JobImage(1L, 1L, "ett"),
      "eh26 0hq",
    )
  }

  @PreAuthorize("hasRole('WORK_READINESS_EDIT') or hasRole('ROLE_EDUCATION_WORK_PLAN_EDIT')")
  @GetMapping("/test/list")
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
  fun getEmployerlist(
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    pageNo: Int,
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    pageSize: Int,
    @RequestParam
    @Parameter(description = "The identifier of the establishment(prison) to get the active bookings for", required = true)
    sortBy: String,
  ): MutableList<SimplifiedJobEmployer>? {
    return jobEmployerService.getPagingList(pageNo, pageSize, sortBy)
  }
}
