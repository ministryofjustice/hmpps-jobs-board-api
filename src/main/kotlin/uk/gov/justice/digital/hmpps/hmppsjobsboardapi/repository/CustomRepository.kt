package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository

import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.JobsBoardProfile

interface CustomRepository {
  fun findJobsBoardProfileByEntityGraph(offenderId: String): JobsBoardProfile?
}
