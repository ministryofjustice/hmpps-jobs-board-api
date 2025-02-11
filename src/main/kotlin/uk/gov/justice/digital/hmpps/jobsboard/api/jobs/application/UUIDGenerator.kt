package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.springframework.stereotype.Service
import java.util.*

@Service
class UUIDGenerator {
  fun generate(): String = UUID.randomUUID().toString()
}
