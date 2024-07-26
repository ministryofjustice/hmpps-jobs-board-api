package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class JobRetrieverShould : TestBase() {

  @InjectMocks
  private lateinit var jobRetriever: JobRetriever

  @Test
  fun `return a Job when found`() {
    `when`(jobRepository.findById(EntityId("f17355ca-5806-4f68-b60a-bafb1568120e"))).thenReturn(
      Optional.of(
        expectedJob,
      ),
    )

    val actualJob: Job = jobRetriever.retrieve("f17355ca-5806-4f68-b60a-bafb1568120e")

    verify(jobRepository, times(1)).findById(EntityId("f17355ca-5806-4f68-b60a-bafb1568120e"))
    assertEquals(expectedJob, actualJob)
  }
}
