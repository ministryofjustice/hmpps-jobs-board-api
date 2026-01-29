package uk.gov.justice.digital.hmpps.jobsboard.api.exceptions

import java.util.function.Supplier

class NotFoundException(var offenderId: String) :
  Exception("CM profile does not exist for offender $offenderId"),
  Supplier<Throwable> {
  override fun get(): Throwable = throw NotFoundException(offenderId)
}

class InvalidStateException(var offenderId: String) :
  Exception("CM profile is in an invalid state for  $offenderId"),
  Supplier<Throwable> {
  override fun get(): Throwable = throw InvalidStateException(offenderId)
}
