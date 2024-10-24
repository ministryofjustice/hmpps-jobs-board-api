package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.EntityId
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.JobMother.amazonForkliftOperator
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.OsPlacesApiClient
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.PostcodesRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.infrastructure.OsPlacesApiDPA
import java.util.UUID.randomUUID

@ExtendWith(MockitoExtension::class)
class PostcodeLocationServiceShould {
  @Mock
  private lateinit var postcodesRepository: PostcodesRepository

  @Mock
  private lateinit var uuidGenerator: UUIDGenerator

  @Mock
  private lateinit var osPlacesAPIClient: OsPlacesApiClient

  @InjectMocks
  private lateinit var postcodeLocationService: PostcodeLocationService

  val postcodeId = randomUUID().toString()

  val expectedPostcode = Postcode(
    id = EntityId(postcodeId),
    code = amazonForkliftOperator.postcode,
    xCoordinate = 1.23f,
    yCoordinate = 4.56f,
  )

  val expectedPostcodeWithNullCoordinates = Postcode(
    id = EntityId(postcodeId),
    code = amazonForkliftOperator.postcode,
    xCoordinate = null,
    yCoordinate = null,
  )

  val expectedLocation = OsPlacesApiDPA(
    postcode = amazonForkliftOperator.postcode,
    xCoordinate = expectedPostcode.xCoordinate,
    yCoordinate = expectedPostcode.yCoordinate,
  )

  @Nested
  @DisplayName("Given a postcode does not exist")
  inner class GivenPostcodeNotExisting {
    @Test
    fun `save a postcode with coordinates`() {
      whenever(uuidGenerator.generate())
        .thenReturn(postcodeId)
      whenever(osPlacesAPIClient.getAddressesFor(amazonForkliftOperator.postcode))
        .thenReturn(expectedLocation)
      whenever(postcodesRepository.existsByCode(amazonForkliftOperator.postcode))
        .thenReturn(false)

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

  @Nested
  @DisplayName("Given a postcode exists")
  inner class GivenPostcodeExisting {
    @BeforeEach
    fun setUp() {
      whenever(postcodesRepository.existsByCode(amazonForkliftOperator.postcode))
        .thenReturn(true)
    }

    @Nested
    @DisplayName("And the stored coordinates are not null")
    inner class AndStoredCoordinatesAreNotNull {
      @Test
      fun `not save a postcode with coordinates`() {
        whenever(postcodesRepository.findByCode(amazonForkliftOperator.postcode))
          .thenReturn(expectedPostcode)

        postcodeLocationService.save(amazonForkliftOperator.postcode)

        verify(postcodesRepository, never()).save(any())
        verify(osPlacesAPIClient, never()).getAddressesFor(any())
      }
    }

    @Nested
    @DisplayName("And the stored coordinates are null")
    inner class AndStoredCoordinatesAreNull {
      @Test
      fun `Update postcode with fresh coordinates`() {
        whenever(uuidGenerator.generate())
          .thenReturn(postcodeId)
        whenever(postcodesRepository.findByCode(amazonForkliftOperator.postcode))
          .thenReturn(expectedPostcodeWithNullCoordinates)
        whenever(osPlacesAPIClient.getAddressesFor(amazonForkliftOperator.postcode))
          .thenReturn(expectedLocation)

        postcodeLocationService.save(amazonForkliftOperator.postcode)

        verify(postcodesRepository).save(expectedPostcode)
        verify(osPlacesAPIClient).getAddressesFor(amazonForkliftOperator.postcode)
      }
    }
  }
}
