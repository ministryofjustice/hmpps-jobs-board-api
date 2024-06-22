package uk.gov.justice.digital.hmpps.jobsboard.api.repository

interface CustomRepository {
  fun findJobsBoardProfileByEntityGraph(offenderId: String): JobsBoardProfile?
}
