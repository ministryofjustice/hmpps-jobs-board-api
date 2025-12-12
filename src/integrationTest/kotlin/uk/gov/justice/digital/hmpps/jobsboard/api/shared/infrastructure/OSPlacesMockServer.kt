package uk.gov.justice.digital.hmpps.jobsboard.api.shared.infrastructure

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.slf4j.LoggerFactory
import org.springframework.web.util.UriComponentsBuilder
import uk.gov.justice.digital.hmpps.jobsboard.api.jobs.domain.Postcode

private const val OS_PLACES_WIREMOCK_PORT = 8093

class OSPlacesMockServer(private val apiKey: String) : WireMockServer(OS_PLACES_WIREMOCK_PORT) {
  private val log = LoggerFactory.getLogger(this::class.java)

  fun stubGetAddressesForPostcode(postcode: Postcode) {
    val json = """{
      "header": {
        "uri": "https://api.os.uk/search/places/v1/postcode?postcode=${postcode.code}",
        "query": "postcode=${postcode.code}",
        "offset": 0,
        "totalresults": 29,
        "format": "JSON",
        "dataset": "DPA",
        "lr": "EN,CY",
        "maxresults": 100,
        "epoch": "112",
        "lastupdate": "2024-09-20",
        "output_srs": "EPSG:27700"
      },
      "results": [
        {
          "DPA": {
            "UPRN": "100120991537",
            "UDPRN": "22659267",
            "ADDRESS": "1, THE STREET, MORTIMER, READING, AG12 1RW",
            "BUILDING_NUMBER": "1",
            "THOROUGHFARE_NAME": "THE STREET",
            "DEPENDENT_LOCALITY": "MORTIMER",
            "POST_TOWN": "READING",
            "POSTCODE": "${postcode.code}",
            "RPC": "1",
            "X_COORDINATE": ${postcode.xCoordinate},
            "Y_COORDINATE": ${postcode.yCoordinate},
            "STATUS": "APPROVED",
            "LOGICAL_STATUS_CODE": "1",
            "CLASSIFICATION_CODE": "RD03",
            "CLASSIFICATION_CODE_DESCRIPTION": "Semi-Detached",
            "LOCAL_CUSTODIAN_CODE": 3940,
            "LOCAL_CUSTODIAN_CODE_DESCRIPTION": "WILTSHIRE",
            "COUNTRY_CODE": "E",
            "COUNTRY_CODE_DESCRIPTION": "This record is within England",
            "POSTAL_ADDRESS_CODE": "D",
            "POSTAL_ADDRESS_CODE_DESCRIPTION": "A record which is linked to PAF",
            "BLPU_STATE_CODE": "2",
            "BLPU_STATE_CODE_DESCRIPTION": "In use",
            "TOPOGRAPHY_LAYER_TOID": "osgb1000015844938",
            "WARD_CODE": "E05013472",
            "PARISH_CODE": "E04011768",
            "LAST_UPDATE_DATE": "01/05/2021",
            "ENTRY_DATE": "15/01/2002",
            "BLPU_STATE_DATE": "12/12/2007",
            "LANGUAGE": "EN",
            "MATCH": 1.0,
            "MATCH_DESCRIPTION": "EXACT",
            "DELIVERY_POINT_SUFFIX": "1A"
          }
        },
        {
          "DPA": {
            "UPRN": "100120991538",
            "UDPRN": "22659277",
            "ADDRESS": "2, A ROAD, EASTERTON, READING, AG12 1RW",
            "BUILDING_NUMBER": "2",
            "THOROUGHFARE_NAME": "A ROAD",
            "DEPENDENT_LOCALITY": "EASTERTON",
            "POST_TOWN": "READING",
            "POSTCODE": "${postcode.code}",
            "RPC": "1",
            "X_COORDINATE": 401017.0,
            "Y_COORDINATE": 154112.0,
            "STATUS": "APPROVED",
            "LOGICAL_STATUS_CODE": "1",
            "CLASSIFICATION_CODE": "RD03",
            "CLASSIFICATION_CODE_DESCRIPTION": "Semi-Detached",
            "LOCAL_CUSTODIAN_CODE": 3940,
            "LOCAL_CUSTODIAN_CODE_DESCRIPTION": "WILTSHIRE",
            "COUNTRY_CODE": "E",
            "COUNTRY_CODE_DESCRIPTION": "This record is within England",
            "POSTAL_ADDRESS_CODE": "D",
            "POSTAL_ADDRESS_CODE_DESCRIPTION": "A record which is linked to PAF",
            "BLPU_STATE_CODE": "2",
            "BLPU_STATE_CODE_DESCRIPTION": "In use",
            "TOPOGRAPHY_LAYER_TOID": "osgb1000015844937",
            "WARD_CODE": "E05013472",
            "PARISH_CODE": "E04011768",
            "LAST_UPDATE_DATE": "01/05/2021",
            "ENTRY_DATE": "15/01/2002",
            "BLPU_STATE_DATE": "12/12/2007",
            "LANGUAGE": "EN",
            "MATCH": 1.0,
            "MATCH_DESCRIPTION": "EXACT",
            "DELIVERY_POINT_SUFFIX": "1P"
          }
        },
        {
          "DPA": {
            "UPRN": "100120991539",
            "UDPRN": "22659286",
            "ADDRESS": "3, PARK ROAD, WHITLEY, READING, AG12 1RW",
            "BUILDING_NUMBER": "3",
            "THOROUGHFARE_NAME": "PARK ROAD",
            "DEPENDENT_LOCALITY": "WHITLEY",
            "POST_TOWN": "READING",
            "POSTCODE": "${postcode.code}",
            "RPC": "1",
            "X_COORDINATE": 401003.0,
            "Y_COORDINATE": 154111.0,
            "STATUS": "APPROVED",
            "LOGICAL_STATUS_CODE": "1",
            "CLASSIFICATION_CODE": "RD03",
            "CLASSIFICATION_CODE_DESCRIPTION": "Semi-Detached",
            "LOCAL_CUSTODIAN_CODE": 3940,
            "LOCAL_CUSTODIAN_CODE_DESCRIPTION": "WILTSHIRE",
            "COUNTRY_CODE": "E",
            "COUNTRY_CODE_DESCRIPTION": "This record is within England",
            "POSTAL_ADDRESS_CODE": "D",
            "POSTAL_ADDRESS_CODE_DESCRIPTION": "A record which is linked to PAF",
            "BLPU_STATE_CODE": "2",
            "BLPU_STATE_CODE_DESCRIPTION": "In use",
            "TOPOGRAPHY_LAYER_TOID": "osgb1000015844933",
            "WARD_CODE": "E05013472",
            "PARISH_CODE": "E04011768",
            "LAST_UPDATE_DATE": "01/05/2021",
            "ENTRY_DATE": "15/01/2002",
            "BLPU_STATE_DATE": "12/12/2007",
            "LANGUAGE": "EN",
            "MATCH": 1.0,
            "MATCH_DESCRIPTION": "EXACT",
            "DELIVERY_POINT_SUFFIX": "1Y"
          }
        }
      ]}
    """.trimIndent()

    val uri = UriComponentsBuilder
      .fromPath("/postcode")
      .queryParam("postcode", postcode.code)
      .queryParam("key", apiKey)
      .encode()
      .build()
      .toUriString()

    stubFor(
      get(urlEqualTo(uri))
        .willReturn(
          WireMock.aResponse().withHeader(
            "Content-Type",
            "application/json",
          ).withBody(
            json,
          ).withStatus(200),
        ),
    )

    log.info("Wiremock endpoint set to: $uri")
  }

  fun stubGetAddressesForPostcodeNotFound(postcode: Postcode) {
    val uri = UriComponentsBuilder
      .fromPath("/postcode")
      .queryParam("postcode", postcode.code)
      .queryParam("key", apiKey)
      .encode()
      .build()
      .toUriString()

    stubFor(
      get(urlEqualTo(uri))
        .willReturn(
          WireMock.aResponse()
            .withStatus(200) // <-- Return 200
            .withHeader("Content-Type", "application/json")
            .withBody(
              """{
                "header" : {
                "uri" : "https://api.os.uk/search/places/v1/postcode?postcode="${postcode.code}",
                "query" : "postcode=${postcode.code}",
                "offset" : 0,
                "totalresults" : 0,
                "format" : "JSON",
                "dataset" : "DPA",
                "lr" : "EN,CY",
                "maxresults" : 100,
                "epoch" : "123",
                "lastupdate" : "2025-12-11",
                "output_srs" : "EPSG:27700"
              }
            }""",
            ),
        ),
    )
  }
}
