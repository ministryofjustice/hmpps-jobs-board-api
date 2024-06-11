package uk.gov.justice.digital.hmpps.jobsboard.api

import java.io.File

class TestData {
  companion object {
    val createdByString = "createdBy"
    val offenderIdString = "offenderId"
    val modifiedByString = "modifiedBy"
    val prisonIdString = "prisonId"
    val prisonNameString = "prisonName"
    val offenderId_A1234AB = "A1234AB"
    val offenderId_A1234AC = "A1234AC"
    val offenderId_A1234AD = "A1234AC"
    val createInvalidReasonToNotGetWork =
      File("src/test/resources/testdata/CreateProfile_InvalidReasonToNotGetWork.json")
        .inputStream()
        .readBytes()
        .toString(Charsets.UTF_8)
  }
}
