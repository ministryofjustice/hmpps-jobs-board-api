package uk.gov.justice.digital.hmpps.jobsboard.api.integration.resource

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedJobEmployer
import uk.gov.justice.digital.hmpps.jobsboard.api.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.jobsboard.api.integration.util.TestData
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileAndJobDetailDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversProfileAndJobDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversCommonSearchDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PLIntrestedJobsClosingSoonListDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.JobEmployerRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversJobRepository
import uk.gov.justice.digital.hmpps.jobsboard.api.repository.PrisonLeaversProfileRepository

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PrisonerProfileIntTest : IntegrationTestBase() {
  @Autowired
  lateinit var prisonLeaversJobRepository: PrisonLeaversJobRepository

  @Autowired
  lateinit var prisonLeaversProfileRepository: PrisonLeaversProfileRepository

  @Autowired
  lateinit var jobEmployerRepository: JobEmployerRepository

  @Autowired
  lateinit var objectMapper: ObjectMapper

  var offenderId = "a123456"

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
      object : TypeReference<PrisonLeaversCommonSearchDTO>() {},
    )
    val result = restTemplate.exchange(
      "/candidate-matching/matched-jobs/closing-soon",
      HttpMethod.POST,
      HttpEntity<PrisonLeaversCommonSearchDTO>(
        prisonLeaverSearch,
        setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW")),
      ),
      PLIntrestedJobsClosingSoonListDTO::class.java,
    )
    Assertions.assertThat(result).isNotNull
    deleteAllPrisonLeavers()
  }

  fun postPrisonLeavers() {
    var employer1 = objectMapper.readValue(
      TestData.employerListToCreate,
      object : TypeReference<SimplifiedJobEmployer>() {},
    )
    var employer2 = objectMapper.readValue(
      TestData.employerListToCreate,
      object : TypeReference<SimplifiedJobEmployer>() {},
    )
    val savedEmployer1 = jobEmployerRepository.save(employer1)
    employer2.postCode = "eh26 0hq"
    val savedEmployer2 = jobEmployerRepository.saveAndFlush(employer2)

    val prisonJobList = objectMapper.readValue(
      TestData.createPrisonerJob,
      object : TypeReference<List<PrisonLeaversJobDTO>>() {},
    )

    val listIterator = prisonJobList.listIterator()
    while (listIterator.hasNext()) {
      val prisonLeaversJob = listIterator.next()
      if (listIterator.hasNext()) {
        prisonLeaversJob.employerId = savedEmployer1.id
      } else {
        prisonLeaversJob.employerId = savedEmployer2.id
      }
      val result = restTemplate.exchange(
        "/candidate-matching/job",
        HttpMethod.POST,
        HttpEntity<PrisonLeaversJobDTO>(
          prisonLeaversJob,
          setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW")),
        ),
        PrisonLeaversJob::class.java,
      )
      Assertions.assertThat(result).isNotNull
      var PrisonLeaversProfileAndJobDTO = PrisonLeaversProfileAndJobDTO(offenderId, result.body.id)
      val resultLeaver = restTemplate.exchange(
         "/candidate-matching/matched-jobs",
        HttpMethod.POST,
        HttpEntity<PrisonLeaversProfileAndJobDTO>(
          PrisonLeaversProfileAndJobDTO,
          setAuthorisation(roles = listOf("ROLE_WORK_READINESS_EDIT", "ROLE_WORK_READINESS_VIEW")),
        ),
        PrisonLeaversProfileAndJobDetailDTO::class.java,
      )
      Assertions.assertThat(resultLeaver).isNotNull
    }
  }

  fun deleteAllPrisonLeavers() {
    jobEmployerRepository.deleteAll()
    prisonLeaversProfileRepository.deleteAll()
    prisonLeaversJobRepository.deleteAll()
  }
}
