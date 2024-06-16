package uk.gov.justice.digital.hmpps.educationandworkplanapi.app.testcontainers

import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.io.IOException
import java.net.ServerSocket
import java.util.function.Consumer

object PostgresContainer {
  val instance: PostgreSQLContainer<Nothing>? by lazy { startPostgresqlContainer() }
  var cmd: Consumer<CreateContainerCmd> =
    Consumer<CreateContainerCmd> { e ->
      e.withHostConfig(HostConfig().withPortBindings(PortBinding(Ports.Binding.bindPort(5433), ExposedPort(5432))))
    }

  private fun startPostgresqlContainer(): PostgreSQLContainer<Nothing>? =
    if (checkPostgresRunning().not()) {
      PostgreSQLContainer<Nothing>(DockerImageName.parse("circleci/postgres:9.5.15-postgis").asCompatibleSubstituteFor("postgres")).apply {
        withEnv("HOSTNAME_EXTERNAL", "localhost")
        withExposedPorts(5432)
        withDatabaseName("job-board")
        withUsername("job-board")
        withPassword("job-board")
        withCreateContainerCmdModifier(cmd)
        setWaitStrategy(Wait.forListeningPort())
        withReuse(true)
        start()
      }
    } else {
      null
    }

  private fun checkPostgresRunning(): Boolean =
    try {
      val serverSocket = ServerSocket(5433)
      serverSocket.localPort == 0
    } catch (e: IOException) {
      true
    }
}
