package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Job
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class JobRetrieverShould : TestBase() {

  @InjectMocks
  private lateinit var jobRetriever: JobRetriever

  @Test
  fun `return a Job when found`() {
    `when`(jobRepository.findById(amazonForkliftOperator.id)).thenReturn(
      Optional.of(
        amazonForkliftOperator,
      ),
    )

    val actualJob: Job = jobRetriever.retrieve(amazonForkliftOperator.id.id)

    verify(jobRepository, times(1)).findById(amazonForkliftOperator.id)
    assertEquals(amazonForkliftOperator, actualJob)
  }
}
