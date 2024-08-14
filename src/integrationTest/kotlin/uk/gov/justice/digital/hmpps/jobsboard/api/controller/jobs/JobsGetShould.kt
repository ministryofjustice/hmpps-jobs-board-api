package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus.CREATED
import java.util.*

class JobsGetShould : JobsTestCase() {

  @Test
  fun `retrieve an existing Job`() {
    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    val jobId = assertAddJobIsCreated(body = amazonForkliftOperatorJobBody)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = amazonForkliftOperatorJobResponse(jobCreationTime),
    )
  }

  @Test
  fun `return null on empty optional fields`() {
    assertAddEmployer(
      id = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    val jobId = assertAddJobIsCreated(body = tescoWarehouseHandlerJobBody)

    assertGetJobIsOK(
      jobId = jobId,
      expectedResponse = tescoWarehouseHandlerJobBody,
    )
  }

  @Test
  fun `retrieve a paginated empty list of Jobs when none registered`() {
    assertGetJobIsOK(
      expectedResponse = expectedResponseListOf(),
    )
  }

  @Test
  fun `retrieve a default paginated Jobs list`(){
    assertAddEmployer(
      id = "89de6c84-3372-4546-bbc1-9d1dc9ceb354",
      body = tescoBody,
      expectedStatus = CREATED,
    )

    assertAddJobIsCreated(
      body = tescoWarehouseHandlerJobBody
    )

    assertAddEmployer(
      id = "bf392249-b360-4e3e-81a0-8497047987e8",
      body = amazonBody,
      expectedStatus = CREATED,
    )

    assertAddJobIsCreated(
      body = amazonForkliftOperatorJobBody
    )

    assertGetJobIsOK(
      parameters = "page=1&size=1",
      expectedResponse = expectedResponseListOf(size = 1, page = 1, totalElements = 2, tescoBody),
    )
  }
}
