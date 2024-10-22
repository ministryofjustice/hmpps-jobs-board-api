package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.OSPlacesAPIClient
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.OSPlacesAPIDPA
import java.util.UUID.randomUUID

@ExtendWith(MockitoExtension::class)
class PostcodeLocationServiceShould {
  @Mock
  private lateinit var postcodesRepository: PostcodesRepository

  @Mock
  private lateinit var uuidGenerator: UUIDGenerator

  @Mock
  private lateinit var osPlacesAPIClient: OSPlacesAPIClient

  @InjectMocks
  private lateinit var postcodeLocationService: PostcodeLocationService

  @Nested
  @DisplayName("Given a postcode does not exist")
  inner class GivenPostcodeNotExisting {
    @Test
    fun `save a postcode with coordinates`() {
      val postcodeId = randomUUID().toString()
      whenever(uuidGenerator.generate())
        .thenReturn(postcodeId)
      val expectedPostcode = Postcode(
        id = EntityId(postcodeId),
        code = amazonForkliftOperator.postcode,
        xCoordinate = 1.23,
        yCoordinate = 4.56,
      )

      val expectedLocation = OSPlacesAPIDPA(
        postcode = amazonForkliftOperator.postcode,
        xCoordinate = expectedPostcode.xCoordinate,
        yCoordinate = expectedPostcode.yCoordinate,
      )
      whenever(osPlacesAPIClient.getAddressesFor(amazonForkliftOperator.postcode))
        .thenReturn(expectedLocation)

      postcodeLocationService.save(amazonForkliftOperator.postcode)

      val locationCaptor = argumentCaptor<String>()
      verify(osPlacesAPIClient).getAddressesFor(locationCaptor.capture())
      val actualLocation = locationCaptor.firstValue

      val postcodeCaptor = argumentCaptor<Postcode>()
      verify(postcodesRepository).save(postcodeCaptor.capture())
      val actualPostcode = postcodeCaptor.firstValue

      assertThat(actualLocation).isEqualTo(amazonForkliftOperator.postcode)
      assertThat(actualPostcode).isEqualTo(expectedPostcode)
    }
  }
}
