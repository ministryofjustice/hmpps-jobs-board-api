package uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure

import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class RepositoryCleaner(private val dataSource: DataSource) {
  fun truncateJobsAndEmployers() = truncateTable("jobs", "employers")

  @Suppress("SameParameterValue")
  private fun truncateTable(vararg tableName: String) = tableName.joinToString().run {
    dataSource.connection.use { conn -> conn.createStatement().use { it.execute("truncate table $this cascade") } }
  }
}
