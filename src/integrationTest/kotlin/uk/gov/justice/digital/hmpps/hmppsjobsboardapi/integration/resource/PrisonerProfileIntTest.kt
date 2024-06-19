package uk.gov.justice.digital.hmpps.jobsboard.api.integration.resource

import uk.gov.justice.digital.hmpps.jobsboard.api.integration.IntegrationTestBase

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PrisonerProfileIntTest : IntegrationTestBase() {
/*
  @Autowired
  lateinit var prisonLeaversJobRepository: PrisonLeaversJobRepository

  @Autowired
  lateinit var prisonLeaversProfileRepository: PrisonLeaversProfileRepository

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @Test
  fun `Post prison leavers profile`() {
    postPrisonLeavers()
    deleteAllPrisonLeavers()
  }

  @Test
  fun `Search for a prison leavers profile`() {
    postPrisonLeavers()
    val prisonLeaverSearch = objectMapper.readValue(
      TestData.prisonLeaversJobsClosingSoonSearch,
      object : TypeReference<PrisonLeaversSearchDTO>() {},
    )
    val result = restTemplate.exchange(
      "/candidate-matching/matched-jobs/closing-soon",
      HttpMethod.POST,
      HttpEntity<PrisonLeaversSearchDTO>(
        prisonLeaverSearch,
        setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW")),
      ),
      PrisonLeaversSearchResultListDTO::class.java,
    )
    assertThat(result).isNotNull
    deleteAllPrisonLeavers()
  }
  fun postPrisonLeavers() {
    val prisonLeaverList = objectMapper.readValue(
      TestData.prisonerProfileToCreate,
      object : TypeReference<List<PrisonLeaversProfileDTO>>() {},
    )
    val listIterator = prisonLeaverList.listIterator()
    while (listIterator.hasNext()) {
      val prisonLeavers = listIterator.next()
      val result = restTemplate.exchange(
        "/candidate-matching/matched-jobs",
        HttpMethod.POST,
        HttpEntity<PrisonLeaversProfileDTO>(
          prisonLeavers,
          setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW")),
        ),
        PrisonLeaversProfileAndJobsDTO::class.java,
      )
      assertThat(result).isNotNull
    }
  }
  fun deleteAllPrisonLeavers() {
    prisonLeaversProfileRepository.deleteAll()
    prisonLeaversJobRepository.deleteAll()
  }*/
}
