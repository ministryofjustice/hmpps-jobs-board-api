package uk.gov.justice.digital.hmpps.jobsboard.api.employers.application

import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.DataValidationException
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.application.ErrorDetail
import java.time.Instant

class EmployerValidationException : DataValidationException {
  constructor(message: String, timestamp: Instant) : super(message, timestamp)
  constructor(message: String, errorDetails: List<ErrorDetail>, timestamp: Instant) : super(message, timestamp, errorDetails)

  companion object {
    val DuplicateEmployerError =
      ErrorDetail("name", "DUPLICATE_EMPLOYER", "The name provided already exists. Please choose a different name.")

    fun duplicateEmployer(timestamp: Instant) = EmployerValidationException("Duplicate Employer", listOf(DuplicateEmployerError), timestamp)
  }
}
