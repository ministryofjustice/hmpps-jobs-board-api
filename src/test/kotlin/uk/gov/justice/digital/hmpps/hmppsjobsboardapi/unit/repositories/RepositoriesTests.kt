package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.unit.repositories

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.config.DpsPrincipal
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.repository.JobsBoardProfileRepository

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
    var dpsPrincipal: DpsPrincipal = DpsPrincipal("test_td", "test_id")
    whenever(authentication?.principal).thenReturn(dpsPrincipal)
    whenever(securityContext?.authentication).thenReturn(authentication)
    SecurityContextHolder.setContext(securityContext)
    jbRepository.deleteAll()
  }

  @Test
  fun `When Job Profile is created with education and qualification`() {
  }
}
