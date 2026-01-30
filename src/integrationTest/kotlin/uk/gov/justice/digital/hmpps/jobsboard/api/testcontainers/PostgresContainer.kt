package uk.gov.justice.digital.hmpps.jobsboard.api.testcontainers

import org.slf4j.LoggerFactory
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.postgresql.PostgreSQLContainer
import java.io.IOException
import java.net.ServerSocket

object PostgresContainer {
  val flywayContainer: PostgreSQLContainer? by lazy { startPostgresqlContainer() }
  val repositoryContainer: PostgreSQLContainer? by lazy { startPostgresqlContainer() }

  private fun startPostgresqlContainer(): PostgreSQLContainer? {
    if (isPostgresRunning()) {
      log.warn("Using existing PostgreSQL database")
      return null
    }

    log.info("Creating a TestContainers PostgreSQL database")

    return PostgreSQLContainer("postgres:16.2").apply {
      withEnv("HOSTNAME_EXTERNAL", "localhost")
      withDatabaseName("job-board")
      withUsername("job-board")
      withPassword("job-board")
      setWaitStrategy(Wait.forListeningPort())
      withReuse(true)
      start()
    }
  }

  private fun isPostgresRunning(): Boolean = try {
    val serverSocket = ServerSocket(5432)
    serverSocket.localPort == 0
  } catch (_: IOException) {
    log.warn("A PostgreSQL database is running")
    true
  }

  private val log = LoggerFactory.getLogger(this::class.java)
}
