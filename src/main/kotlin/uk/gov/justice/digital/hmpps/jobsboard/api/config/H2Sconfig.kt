import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

// Only activate this in the "dev" profile
@Configuration
@Profile("test")
class H2ServerConfiguration {

  // TCP port for remote connections, default 9092
  @Value("\${h2.tcp.port:9092}")
  private lateinit var h2TcpPort: String

  // Web port, default 8082
  @Value("\${h2.web.port:8082}")
  private lateinit var h2WebPort: String

  /**
   * TCP connection to connect with SQL clients to the embedded h2 database.
   *
   * Connect to "jdbc:h2:tcp://localhost:9092/mem:testdb", username "sa", password empty.
   */
  @Bean
  @ConditionalOnExpression("\${h2.tcp.enabled:false}")
  fun h2TcpServer(): Server {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2TcpPort).start()
  }

  /**
   * Web console for the embedded h2 database.
   *
   * Go to http://localhost:8082 and connect to the database "jdbc:h2:mem:testdb", username "sa", password empty.
   */
  @Bean
  @ConditionalOnExpression("\${h2.web.enabled:true}")
  fun h2WebServer(): Server {
    return Server.createWebServer("-web", "-webAllowOthers", "-webPort", h2WebPort).start()
  }
}

