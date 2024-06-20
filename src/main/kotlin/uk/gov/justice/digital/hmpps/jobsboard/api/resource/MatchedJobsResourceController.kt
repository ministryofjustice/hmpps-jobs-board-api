package uk.gov.justice.digital.hmpps.jobsboard.api.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.JobEmployerDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileAndJobsDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchResultListDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.service.PrisonLeaversProfileService

@Validated
@RestController
@RequestMapping("/candidate-matching/matched-jobs", produces = [MediaType.APPLICATION_JSON_VALUE])
class MatchedJobsResourceController(
  private val prisonLeaversProfileService: PrisonLeaversProfileService,
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
  fun jobsMatchedbyOffenderId(
    @Valid
    @RequestBody
    requestDTO: PrisonLeaversProfileDTO,
  ): PrisonLeaversProfileAndJobsDTO {
    var response = prisonLeaversProfileService.createOrUpdatePrisonLeaversProfile(requestDTO)
    return response
  }

  @PreAuthorize("hasRole('WORK_READINESS_EDIT')")
  @PostMapping("/closing-soon")
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
    requestDTO: PrisonLeaversSearchDTO,
  ): PrisonLeaversSearchResultListDTO? {
    var plist = prisonLeaversProfileService.searchPrisonLeaversProfile(requestDTO)
    var prisonLeaversSearchResultListDTO = PrisonLeaversSearchResultListDTO(plist)
    return prisonLeaversSearchResultListDTO
  }
}
