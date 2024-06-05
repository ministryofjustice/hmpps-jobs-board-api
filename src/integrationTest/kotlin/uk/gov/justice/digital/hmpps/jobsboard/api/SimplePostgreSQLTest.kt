package org.testcontainers.junit.postgresql

import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.LogManager
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

class SimplePostgreSQLTest {

  var postgres: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgis/postgis:16-3.4-alpine").asCompatibleSubstituteFor("postgres"))

  @BeforeAll
  fun beforeAll() {
      postgres.start()
      val resultSet: ResultSet = performQuery(postgres, "SELECT 1")
      val resultSetInt = resultSet.getInt(1)
      Assertions.assertThat(resultSetInt).`as`("A basic SELECT query succeeds").isEqualTo(1)
      assertHasCorrectExposedAndLivenessCheckPorts(postgres)

  }

  @AfterAll
  fun afterAll() {
    postgres.stop()
  }
  @Test
  @Throws(SQLException::class)
  fun testSimple() {
    PostgreSQLContainer(DockerImageName.parse("postgis/postgis:16-3.4-alpine").asCompatibleSubstituteFor("postgres")).use { postgres ->
      postgres.start()
      val resultSet: ResultSet = performQuery(postgres, "SELECT 1")
      val resultSetInt = resultSet.getInt(1)
      Assertions.assertThat(resultSetInt).`as`("A basic SELECT query succeeds").isEqualTo(1)
      assertHasCorrectExposedAndLivenessCheckPorts(postgres)
    }
  }

  @Test
  @Throws(SQLException::class)
  fun testCommandOverride() {
    PostgreSQLContainer(PostgreSQLTestImages.POSTGRES_TEST_IMAGE)
      .withCommand("postgres -c max_connections=42").use { postgres ->
        postgres.start()
        val resultSet: ResultSet = performQuery(postgres, "SELECT current_setting('max_connections')")
        val result = resultSet.getString(1)
        Assertions.assertThat(result).`as`("max_connections should be overriden")
          .isEqualTo("42")
      }
  }

  @Test
  @Throws(SQLException::class)
  fun testUnsetCommand() {
    PostgreSQLContainer(PostgreSQLTestImages.POSTGRES_TEST_IMAGE)
      .withCommand("postgres -c max_connections=42")
      .withCommand().use { postgres ->
        postgres.start()
        val resultSet: ResultSet = performQuery(postgres, "SELECT current_setting('max_connections')")
        val result = resultSet.getString(1)
        Assertions.assertThat(result).`as`("max_connections should not be overriden")
          .isNotEqualTo("42")
      }
  }

  @Test
  @Throws(SQLException::class)
  fun testExplicitInitScript() {
    PostgreSQLContainer(PostgreSQLTestImages.POSTGRES_TEST_IMAGE)
      .withInitScript("somepath/init_postgresql.sql").use { postgres ->
        postgres.start()
        val resultSet: ResultSet = performQuery(postgres, "SELECT foo FROM bar")
        val firstColumnValue = resultSet.getString(1)
        Assertions.assertThat(firstColumnValue)
          .`as`("Value from init script should equal real value").isEqualTo("hello world")
      }
  }

  @Test
  fun testWithAdditionalUrlParamInJdbcUrl() {
    PostgreSQLContainer(PostgreSQLTestImages.POSTGRES_TEST_IMAGE)
      .withUrlParam("charSet", "UNICODE").use { postgres ->
        postgres.start()
        val jdbcUrl: String = postgres.getJdbcUrl()
        Assertions.assertThat(jdbcUrl).contains("?")
        Assertions.assertThat(jdbcUrl).contains("&")
        Assertions.assertThat(jdbcUrl).contains("charSet=UNICODE")
      }
  }

  private fun assertHasCorrectExposedAndLivenessCheckPorts(postgres: PostgreSQLContainer<*>?) {
    assertThat(postgres.getExposedPorts()).containsExactly(PostgreSQLContainer.POSTGRESQL_PORT)
    assertThat(postgres.getLivenessCheckPortNumbers())
      .containsExactly(postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT))
  }

  companion object {
    init {
      // Postgres JDBC driver uses JUL; disable it to avoid annoying, irrelevant, stderr logs during connection testing
      LogManager.getLogManager().getLogger("").level = Level.OFF
    }
  }
}