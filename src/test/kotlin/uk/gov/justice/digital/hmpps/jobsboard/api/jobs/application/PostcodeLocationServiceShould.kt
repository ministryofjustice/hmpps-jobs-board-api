package uk.gov.justice.digital.hmpps.jobsboard.api.jobs.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
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

  private val postcodeId = randomUUID().toString()

  @Nested
  @DisplayName("Given a postcode does not exist")
  inner class GivenPostcodeNotExisting {
    @Test
    fun `save a postcode with coordinates`() {
      val postcode = amazonForkliftOperator.postcode!!
      whenever(uuidGenerator.generate())
        .thenReturn(postcodeId)
      whenever(osPlacesAPIClient.getAddressesFor(postcode))
        .thenReturn(expectedLocation)

      postcodeLocationService.save(postcode)

      verify(osPlacesAPIClient).getAddressesFor(postcode)
      verify(postcodesRepository).save(expectedPostcode)
    }
  }

  @Nested
  @DisplayName("Given a postcode exists")
  inner class GivenPostcodeExisting {
    private val postcode = amazonForkliftOperator.postcode!!

    @Nested
    @DisplayName("And the stored coordinates are not null")
    inner class AndStoredCoordinatesAreNotNull {
      @Test
      fun `not save a postcode with coordinates`() {
        whenever(postcodesRepository.findByCode(postcode))
          .thenReturn(expectedPostcode)

        postcodeLocationService.save(postcode)

        verify(osPlacesAPIClient, never()).getAddressesFor(any())
        verify(postcodesRepository, never()).save(any())
      }
    }

    @Nested
    @DisplayName("And the stored coordinates are null")
    inner class AndStoredCoordinatesAreNull {
      @Test
      fun `Update postcode with fresh coordinates`() {
        whenever(postcodesRepository.findByCode(postcode))
          .thenReturn(expectedPostcodeWithNullCoordinates)
        whenever(osPlacesAPIClient.getAddressesFor(postcode))
          .thenReturn(expectedLocation)

        postcodeLocationService.save(postcode)

        verify(osPlacesAPIClient).getAddressesFor(postcode)
        verify(postcodesRepository).save(expectedPostcode)
      }
    }
  }

  @Nested
  @DisplayName("Is Geocoded postcode")
  inner class IsGeocodedPostcode {
    private val postcode = amazonForkliftOperator.postcode!!

    @Test
    fun `geocoded postcode with coordinates exists`() {
      whenever(postcodesRepository.findByCode(postcode))
        .thenReturn(expectedPostcode)

      val actual = postcodeLocationService.isGeoCoded(postcode)

      assertThat(actual).isTrue

      verify(osPlacesAPIClient, never()).getAddressesFor(any())
    }

    @Test
    fun `postcode with null coordinates exists`() {
      whenever(postcodesRepository.findByCode(postcode))
        .thenReturn(expectedPostcodeWithNullCoordinates)

      val actual = postcodeLocationService.isGeoCoded(postcode)

      assertThat(actual).isFalse

      verify(osPlacesAPIClient, never()).getAddressesFor(any())
    }

    @Test
    fun `postcode does not exist and geocoded postcode returned`() {
      whenever(postcodesRepository.findByCode(postcode))
        .thenReturn(null)
      whenever(osPlacesAPIClient.getAddressesFor(postcode))
        .thenReturn(expectedLocation)

      val actual = postcodeLocationService.isGeoCoded(postcode)

      assertThat(actual).isTrue

      verify(osPlacesAPIClient).getAddressesFor(postcode)
    }

    @Test
    fun `postcode does not exist and geocoded postcode not returned`() {
      whenever(postcodesRepository.findByCode(postcode))
        .thenReturn(null)
      whenever(osPlacesAPIClient.getAddressesFor(postcode))
        .thenReturn(expectedLocation)

      val actual = postcodeLocationService.isGeoCoded(postcode)

      assertThat(actual).isTrue

      verify(osPlacesAPIClient).getAddressesFor(postcode)
    }
  }

  private val expectedPostcode = Postcode(
    id = EntityId(postcodeId),
    code = amazonForkliftOperator.postcode!!,
    xCoordinate = 1.23,
    yCoordinate = 4.56,
  )

  private val expectedPostcodeWithNullCoordinates = Postcode(
    id = EntityId(postcodeId),
    code = amazonForkliftOperator.postcode!!,
    xCoordinate = null,
    yCoordinate = null,
  )

  private val expectedLocation = OsPlacesApiDPA(
    postcode = amazonForkliftOperator.postcode!!,
    xCoordinate = expectedPostcode.xCoordinate,
    yCoordinate = expectedPostcode.yCoordinate,
  )
}
