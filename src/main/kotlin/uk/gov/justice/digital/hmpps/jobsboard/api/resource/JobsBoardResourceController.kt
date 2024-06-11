package uk.gov.justice.digital.hmpps.jobsboard.api.resource

import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.jobsboard.api.service.JobsBoardProfileService

@Validated
@RestController
@RequestMapping("/cm/profile", produces = [MediaType.APPLICATION_JSON_VALUE])
class JobsBoardResourceController(
  private val jobsBoardProfileService: JobsBoardProfileService,
)
