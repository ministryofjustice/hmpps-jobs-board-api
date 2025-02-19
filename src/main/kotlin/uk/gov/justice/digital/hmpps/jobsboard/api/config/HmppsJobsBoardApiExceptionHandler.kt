package uk.gov.justice.digital.hmpps.jobsboard.api.config

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application.JobNotFoundException
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.DataValidationException
import uk.gov.justice.digital.hmpps.jobsboard.api.time.TimeProvider
import java.time.Instant
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.ErrorDetail as DataErrorDetail

@RestControllerAdvice
class HmppsJobsBoardApiExceptionHandler(
  val timeProvider: TimeProvider,
) {
  @ExceptionHandler(ValidationException::class)
  fun handleValidationException(e: Exception): ResponseEntity<ErrorResponse> {
    log.info("Validation exception: {}", e.message)
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ErrorResponse(
          status = BAD_REQUEST,
          userMessage = "Validation failure: ${e.message}",
          developerMessage = e.message,
        ),
      )
  }

  @ExceptionHandler(NoResourceFoundException::class)
  fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(NOT_FOUND)
    .body(
      ErrorResponse(
        status = NOT_FOUND,
        userMessage = "No resource found failure: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.info("No resource found exception: {}", e.message) }

  @ExceptionHandler(JobNotFoundException::class)
  fun handleJobNotFoundException(e: JobNotFoundException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(NOT_FOUND)
    .body(
      ErrorResponse(
        status = NOT_FOUND,
        userMessage = "No Job found failure: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.info("No Job found exception: {}", e.message) }

  @ExceptionHandler(DataValidationException::class)
  fun handleDataValidationException(e: DataValidationException): ResponseEntity<DataErrorResponse> {
    log.info("Data Validation exception: {}", e.message)
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        DataErrorResponse(
          status = BAD_REQUEST,
          timestamp = timeProvider.nowAsInstant(),
          userMessage = "Validation failed",
          error = "Bad request: ${e.message}",
          details = e.errorDetails?.map { ErrorDetail.from(it) }?.toList(),
        ),
      )
  }

  @ExceptionHandler(AccessDeniedException::class)
  fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ErrorResponse> = ResponseEntity
    .status(FORBIDDEN)
    .body(
      ErrorResponse(
        status = FORBIDDEN,
        userMessage = "Forbidden: ${e.message}",
        developerMessage = e.message,
      ),
    ).also { log.debug("Forbidden (403) returned: {}", e.message) }

  @ExceptionHandler(IllegalArgumentException::class)
  fun handleIllegalArgumentException(e: Exception): ResponseEntity<ErrorResponse> {
    log.info("Illegal Argument exception: {}", e.message)
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ErrorResponse(
          status = BAD_REQUEST,
          userMessage = "Illegal Argument: ${e.message}",
          developerMessage = e.message,
        ),
      )
  }

  @ExceptionHandler(MissingServletRequestParameterException::class)
  fun handleMissingParams(e: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
    log.info("Missing required parameter: {}", e.message)
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ErrorResponse(
          status = BAD_REQUEST,
          userMessage = "Missing required parameter: ${e.message}",
          developerMessage = e.message,
        ),
      )
  }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
    val errorMessage = e.fieldErrors.map { "${it.field} - ${it.defaultMessage}" }.joinToString(separator = ", ")
    log.info("Method argument not valid: {}", errorMessage)
    return ResponseEntity
      .status(BAD_REQUEST)
      .body(
        ErrorResponse(
          status = BAD_REQUEST,
          userMessage = "Validation failure: $errorMessage",
          developerMessage = errorMessage,
        ),
      )
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> = e.let {
    val errorMessage = "Type mismatch: parameter '${e.name}' with value '${e.value}'"
    ResponseEntity.status(BAD_REQUEST).body(
      ErrorResponse(
        status = BAD_REQUEST,
        userMessage = "Validation failure: $errorMessage",
        developerMessage = errorMessage,
      ),
    )
  }.also { log.info("Method argument type mismatch exception: ${e.message}", e) }

  @ExceptionHandler(java.lang.Exception::class)
  fun handleException(e: java.lang.Exception): ResponseEntity<ErrorResponse?>? {
    log.error("Unexpected exception", e)
    return ResponseEntity
      .status(INTERNAL_SERVER_ERROR)
      .body(
        ErrorResponse(
          status = INTERNAL_SERVER_ERROR,
          userMessage = "Unexpected error: ${e.message}",
          developerMessage = e.message,
        ),
      )
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}

data class ErrorResponse(
  val status: Int,
  val errorCode: Int? = null,
  val userMessage: String? = null,
  val developerMessage: String? = null,
  val moreInfo: String? = null,
) {
  constructor(
    status: HttpStatus,
    errorCode: Int? = null,
    userMessage: String? = null,
    developerMessage: String? = null,
    moreInfo: String? = null,
  ) :
    this(status.value(), errorCode, userMessage, developerMessage, moreInfo)
}

data class DataErrorResponse(
  val status: Int,
  val timestamp: Instant? = null,
  val error: String? = null,
  val details: List<ErrorDetail>? = null,
  val userMessage: String? = null,
  @JsonInclude(JsonInclude.Include.NON_NULL) val errorCode: Int? = null,
  @JsonInclude(JsonInclude.Include.NON_NULL) val developerMessage: String? = null,
  @JsonInclude(JsonInclude.Include.NON_NULL) val moreInfo: String? = null,
) {
  constructor(
    status: HttpStatus,
    timestamp: Instant,
    error: String? = null,
    details: List<ErrorDetail>? = null,
    userMessage: String? = null,
  ) : this(status.value(), timestamp, error, details, userMessage)
}

data class ErrorDetail(
  val field: String,
  val message: String? = null,
  val code: String,
) {
  companion object {
    fun from(errorDetail: DataErrorDetail) = errorDetail.run { ErrorDetail(fieldName, errorMessage, errorCode) }
  }
}
