import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.sql.SQLException


@Configuration
@Profile("dev") // Only activate this in the "dev" profile
public class H2ServerConfiguration {

  /
  @Bean(initMethod = "start", destroyMethod = "stop")
  @Throws(SQLException::class)
  fun h2Server(): Server? {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092")
  }
}