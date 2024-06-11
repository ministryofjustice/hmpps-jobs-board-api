package uk.gov.justice.digital.hmpps.jobsboard.api.repository

import uk.gov.justice.digital.hmpps.jobsboard.api.entity.JobsBoardProfile

interface CustomRepository {
  fun findJobsBoardProfileByEntityGraph(offenderId: String): JobsBoardProfile?
}
