package uk.gov.justice.digital.hmpps.jobsboard.api.controller.sar

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.config.ErrorResponse
import uk.gov.justice.digital.hmpps.jobsboard.api.exceptions.NotFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.application.SubjectAccessRequestService
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ApplicationDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ArchivedDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.ExpressionOfInterestDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARContentDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARFilter
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARSummaryDTO
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

@Validated
@RestController
@RequestMapping("/subject-access-request", produces = [MediaType.APPLICATION_JSON_VALUE])
class SubjectAccessRequestGet(
  private val subjectAccessRequestService: SubjectAccessRequestService,
) {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  @PreAuthorize("hasAnyRole('SAR_DATA_ACCESS', @environment.getProperty('hmpps.sar.additionalAccessRole', 'SAR_DATA_ACCESS') )")
  @GetMapping
  @Operation(
    summary = "Successful return of data fulfilling subject access request for a prisoner",
    description = "The success status is set as the subject access request has been processed correctly",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Request successfully processed - content found with recognised PRN",
        content = [
          Content(
            mediaType = "application/json",
            schema = Schema(implementation = SARSummaryDTO::class),
          ),
        ],
      ),
      ApiResponse(
        responseCode = "204",
        description = "Request successfully processed - no content found with recognised PRN",
        content = [],
      ),
      ApiResponse(
        responseCode = "209",
        description = "Request successfully processed - no content found with CRN identifier",
        content = [Content()],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access - incorrect role",
        content = [Content()],
      ),
    ],
  )
  fun subjectAccess(
    @Parameter(description = "NOMIS Prison Reference Number", example = "A1234BC") @Valid @Pattern(regexp = "^[A-Z]\\d{4}[A-Z]{2}\$") @RequestParam prn: String? = null,
    @Parameter(description = "nDelius Case Reference Number") @RequestParam crn: String? = null,
    @Parameter(description = "Optional parameter denoting minimum date of event occurrence which should be returned in the response") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam fromDate: LocalDate? = null,
    @Parameter(description = "Optional parameter denoting maximum date of event occurrence which should be returned in the response") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam toDate: LocalDate? = null,
  ): ResponseEntity<Any> {
    when {
      prn.isNullOrBlank() && crn.isNullOrBlank() -> "One of prn or crn must be supplied."
      fromDate != null && toDate != null && fromDate.isAfter(toDate) -> "fromDate ($fromDate) cannot be after toDate ($toDate)"
      else -> null
    }?.let {
      return ResponseEntity.badRequest().body(
        ErrorResponse(status = HttpStatus.BAD_REQUEST, userMessage = it, developerMessage = it),
      )
    }

    if (!prn.isNullOrEmpty()) {
      return try {
        val sarFilter = SARFilter(prn, fromDate, toDate)
        val listOfJobApplications: CompletableFuture<List<ApplicationDTO>> = subjectAccessRequestService.fetchApplications(sarFilter)
        val listOfExpressionsOfInterest: CompletableFuture<List<ExpressionOfInterestDTO>> = subjectAccessRequestService.fetchExpressionsOfInterest(sarFilter)
        val listOfArchivedJobs: CompletableFuture<List<ArchivedDTO>> = subjectAccessRequestService.fetchArchivedJobs(sarFilter)

        try {
          CompletableFuture.allOf(listOfJobApplications, listOfExpressionsOfInterest, listOfArchivedJobs).join()
        } catch (e: Exception) {
          return "An error occurred while building data".also { log.error(it, e) }.let { message ->
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
              ErrorResponse(status = HttpStatus.INTERNAL_SERVER_ERROR, userMessage = message, developerMessage = e.message),
            )
          }
        }

        if (listOfJobApplications.get().isEmpty() && listOfExpressionsOfInterest.get().isEmpty() && listOfArchivedJobs.get().isEmpty()) {
          return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(
          SARSummaryDTO(
            SARContentDTO(
              listOfJobApplications.get(),
              listOfExpressionsOfInterest.get(),
              listOfArchivedJobs.get(),
            ),
          ),
        )
      } catch (ex: NotFoundException) {
        ResponseEntity.noContent().build()
      } catch (ex: Exception) {
        log.error("Unexpected exception", ex)
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            userMessage = "An unexpected error occurred while fetching data",
            developerMessage = ex.message,
          ),
        )
      }
    }

    return ResponseEntity.status(209).build()
  }
}
