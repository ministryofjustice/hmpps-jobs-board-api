package uk.gov.justice.digital.hmpps.jobsboard.api.integration.util

import java.io.File

class TestData {
  companion object {
    val createPrisonerJob = File("src/integrationTest/resources/testdata/PrisonerJobListToCreate.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val generalPrisonJobSearch = File("src/integrationTest/resources/testdata/GeneralPrisonSearch.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchOrderByTypeOfWork = File("src/integrationTest/resources/testdata/PrisonSearchOrderByTypeOfWork.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchUnSorted = File("src/integrationTest/resources/testdata/PrisonSearchUnSorted.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchOrderByTypeOfWorkAndPostCode = File("src/integrationTest/resources/testdata/PrisonSearchOrderByTypeOfWorkAndPostCode.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchOrderByPostCode = File("src/integrationTest/resources/testdata/PrisonSearchOrderByPostCode.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val generalPrisonSearchSize2 = File("src/integrationTest/resources/testdata/GeneralPrisonSearchSize2.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val generalPrisonSearchSize4 = File("src/integrationTest/resources/testdata/GeneralPrisonSearchSize4.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchByTypeOfWork = File("src/integrationTest/resources/testdata/PrisonSearchByTypeOfWork.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchByUnAvailableTypeOfWork = File("src/integrationTest/resources/testdata/PrisonSearchByUnAvailableTypeOfWork.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchByAvailablePostCode = File("src/integrationTest/resources/testdata/PrisonSearchByAvailablePostCode.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonSearchByUnAvailablePostCode = File("src/integrationTest/resources/testdata/PrisonSearchByUnAvailablePostCode.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonerProfileToCreate = File("src/integrationTest/resources/testdata/PrisonerProfileToCreate.json").inputStream().readBytes().toString(Charsets.UTF_8)
    val prisonLeaversJobsClosingSoonSearch = File("src/integrationTest/resources/testdata/PrisonLeaversJobsClosingSoonSearch.json").inputStream().readBytes().toString(Charsets.UTF_8)

  }
}
