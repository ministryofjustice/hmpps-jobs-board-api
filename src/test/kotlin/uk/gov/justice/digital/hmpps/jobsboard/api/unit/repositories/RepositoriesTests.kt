package uk.gov.justice.digital.hmpps.jobsboard.api.unit.repositories

import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.jobsboard.api.config.DpsPrincipal
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobsBoardProfileRepository

@DataJpaTest
@ActiveProfiles("test")
class RepositoriesTests @Autowired constructor(
  val jbRepository: JobsBoardProfileRepository,

) {
  @Mock
  private val securityContext: SecurityContext? = null

  @Mock
  private val authentication: Authentication? = null

  @BeforeEach
  fun beforeClass() {
    val dpsPrincipal = DpsPrincipal("test_td", "test_id")
    whenever(authentication?.principal).thenReturn(dpsPrincipal)
    whenever(securityContext?.authentication).thenReturn(authentication)
    SecurityContextHolder.setContext(securityContext)
    jbRepository.deleteAll()
  }

//  @Disabled("Empty test")
//  @Test
//  fun `When Job Profile is created with education and qualification`() {
//  }
}
