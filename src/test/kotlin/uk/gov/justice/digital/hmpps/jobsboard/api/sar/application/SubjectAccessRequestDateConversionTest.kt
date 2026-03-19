package uk.gov.justice.digital.hmpps.jobsboard.api.sar.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.LoggerFactory
import uk.gov.justice.digital.hmpps.subjectaccessrequest.templates.DateConversionHelper
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SubjectAccessRequestDateConversionTest {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  private val dateConversionHelper = DateConversionHelper()

  @Disabled
  @ParameterizedTest
  @ValueSource(
    strings = [
      "2025-03-30T00:59:59Z", // 2025-03-30 12:59am in GMT
      "2025-03-30T01:00:00Z", // 2025-03-30 1am in BST(+1)      1am GMT -> 2am BST, clocks go forward
      "2025-10-26T00:59:59Z", // 2025-10-26 1:59am in BST (+1)
      "2025-10-26T01:00:00Z", // 2025-10-26 1am in GMT;         2am BST -> 1am GMT, clocks go backward
    ],
  )
  fun `Should convert from ISO date-time string to formatted date-time`(dateTimeString: String) {
    // https://www.gov.uk/when-do-the-clocks-change
    // In the UK the clocks go forward 1 hour at 1am on the last Sunday in March, and back 1 hour at 2am on the last Sunday in October.
    // Year	    Clocks go forward	    Clocks go back
    // 2025	    30 March	            26 October
    val zoneId = ZoneId.of("Europe/London")
    val sarReportFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, h:mm:ss a")
    val expectedDateTime = Instant.parse(dateTimeString).atZone(zoneId).toLocalDateTime().format(sarReportFormatter)

    val actualDateTime = dateConversionHelper.convertDates(dateTimeString)

    log.debug("expectedDateTime={}, actualDateTime={}", expectedDateTime, actualDateTime)

    assertThat(actualDateTime).isEqualTo(expectedDateTime)
  }
}
