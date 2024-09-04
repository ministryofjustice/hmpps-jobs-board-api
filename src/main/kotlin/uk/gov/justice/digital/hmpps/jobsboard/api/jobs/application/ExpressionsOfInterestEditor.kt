package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.transaction.annotation.Transactional

class ExpressionsOfInterestEditor {
  // FIXME to implement Expressions-of-Interest

  @Transactional
  fun createWhenNotExist(request: CreateOrDeleteExpressionOfInterestRequest): Boolean {
    // FIXME to implement registering Expression-of-Interest
    var created = false
    throw NotImplementedError("Creation of Expression-of-Interest is NOT yet implemented!")
//    return created
  }

  @Transactional
  fun delete(request: CreateOrDeleteExpressionOfInterestRequest) {
    // FIXME to implement registering Expression-of-Interest
    throw NotImplementedError("Creation of Expression-of-Interest is NOT yet implemented!")
  }
}
