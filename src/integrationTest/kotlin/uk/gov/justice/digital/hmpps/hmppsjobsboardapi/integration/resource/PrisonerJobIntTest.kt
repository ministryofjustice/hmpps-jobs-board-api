package uk.gov.justice.digital.hmpps.jobsboard.api.integration.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedPrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.enums.TypeOfWork
import uk.gov.justice.digital.hmpps.jobsboard.api.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.jobsboard.api.integration.util.TestData
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobListPageDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversPagingDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobEmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversProfileRepository

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PrisonerJobIntTest : IntegrationTestBase() {
  @Autowired
  lateinit var prisonLeaversJobRepository: PrisonLeaversJobRepository

  @Autowired
  lateinit var prisonLeaversProfileRepository: PrisonLeaversProfileRepository

  @Autowired
  lateinit var jobEmployerRepository: JobEmployerRepository

  @Autowired
  lateinit var objectMapper: ObjectMapper

  @Test
  fun `Post jobs for prison leavers`() {
    val prisonJobList = objectMapper.readValue(
      TestData.createPrisonerJob,
      object : TypeReference<List<SimplifiedPrisonLeaversJob>>() {},
    )
    val listIterator = prisonJobList.listIterator()
    while (listIterator.hasNext()) {
      val prisonLeaversJob = listIterator.next()
      val result = restTemplate.exchange(
        "/candidate-matching/job",
        HttpMethod.POST,
        HttpEntity<SimplifiedPrisonLeaversJob>(
          prisonLeaversJob,
          setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW")),
        ),
        SimplifiedPrisonLeaversJob::class.java,
      )
      assertThat(result).isNotNull
    }
  }

  @Test
  fun `Search jobs for prison leavers unsorted`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchUnSorted,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(2)
    assertThat(jobList[0].typeOfWork).isEqualTo(TypeOfWork.CONSTRUCTION)
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers sort by TYPE_OF_WORK`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchOrderByTypeOfWork,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(2)
    assertThat(jobList[0].typeOfWork).isEqualTo(TypeOfWork.BEAUTY)
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers sort by TYPE_OF_WORK_AND_POST_CODE`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchOrderByTypeOfWorkAndPostCode,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(2)
    assertThat(jobList[0].typeOfWork).isEqualTo(TypeOfWork.BEAUTY)
    assertThat(jobList[1].postcode).isEqualTo("eh26 0hq")
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers sort by POST_CODE`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchOrderByPostCode,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(2)
    assertThat(jobList[0].typeOfWork).isEqualTo(TypeOfWork.CONSTRUCTION)
    assertThat(jobList[0].postcode).isEqualTo("eh26 0hq")
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers by available Type of work`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchByTypeOfWork,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(2)
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers by unavailable Type of work`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchByUnAvailableTypeOfWork,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(0)
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers by available PostCode`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchByAvailablePostCode,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(1)
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers by unavailable postcode`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.prisonSearchByUnAvailablePostCode,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(0)
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers for size 2`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.generalPrisonSearchSize2,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(2)
    deleteAllJobs()
  }

  @Test
  fun `Search jobs for prison leavers size 4`() {
    postJobs()
    val generalPrisonJobSearch = objectMapper.readValue(
      TestData.generalPrisonSearchSize4,
      object : TypeReference<PrisonLeaversPagingDTO>() {},
    )

    val result = restTemplate.exchange("/candidate-matching/job/search", HttpMethod.POST, HttpEntity<PrisonLeaversPagingDTO>(generalPrisonJobSearch, setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW"))), PrisonLeaversJobListPageDTO::class.java)
    assertThat(result).isNotNull
    var joblistDTO = result.body
    var jobList = joblistDTO.prisonLeaversJobList
    assertThat(jobList.size).isEqualTo(4)
    deleteAllJobs()
  }

  fun postJobs() {
    val prisonJobList = objectMapper.readValue(
      TestData.createPrisonerJob,
      object : TypeReference<List<SimplifiedPrisonLeaversJob>>() {},
    )
    var employer = prisonJobList[0].employer
    val listIterator = prisonJobList.listIterator()
    while (listIterator.hasNext()) {
      val prisonLeaversJob = listIterator.next()
      val result = restTemplate.exchange(
        "/candidate-matching/job",
        HttpMethod.POST,
        HttpEntity<SimplifiedPrisonLeaversJob>(
          prisonLeaversJob,
          setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW")),
        ),
        SimplifiedPrisonLeaversJob::class.java,
      )

      assertThat(result).isNotNull
    }
  }
  fun deleteAllJobs() {
    prisonLeaversProfileRepository.deleteAll()
    prisonLeaversJobRepository.deleteAll()
    jobEmployerRepository.deleteAll()
  }
}
