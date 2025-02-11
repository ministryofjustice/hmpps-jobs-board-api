package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure.RepositoryTestCase
import java.util.*

class PostcodeRepositoryShould : RepositoryTestCase() {
  @Autowired
  protected lateinit var postcodesRepository: PostcodesRepository

  private val expectedPostcode = postcode("LS11 0AN", 429017.0, 431869.0)

  @Test
  fun `create new postcode`() {
    postcodesRepository.save(expectedPostcode)
  }

  @Nested
  @DisplayName("Given a long username")
  inner class GivenALongUsername {
    private val longUsername = "${"A".repeat(234)}@a.com"

    @BeforeEach
    fun setUp() {
      whenever(auditorProvider.currentAuditor).thenReturn(Optional.of(longUsername))
    }

    @Test
    fun `create new postcode with a long username`() {
      val savedPostcode = postcodesRepository.save(expectedPostcode)
      with(savedPostcode) {
        assertThat(createdBy).hasSize(240)
        assertThat(lastModifiedBy).hasSize(240)
      }
    }
  }

  private fun postcode(postcode: String, xCoordinate: Double, yCoordinate: Double) = Postcode(EntityId(), postcode, xCoordinate, yCoordinate)
}
