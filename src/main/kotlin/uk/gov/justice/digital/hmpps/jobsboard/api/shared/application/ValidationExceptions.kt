package uk.gov.justice.digital.hmpps.jobsboard.api.shared.application

import java.time.Instant

abstract class DataValidationException(
  message: String,
  val timestamp: Instant,
  val errorDetails: List<ErrorDetail>? = null,
) : RuntimeException(message)

data class ErrorDetail(
  val fieldName: String,
  val errorCode: String,
  val errorMessage: String? = null,
)
