package uk.gov.justice.digital.hmpps.jobsboard.api.assemblers

import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Page
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import uk.gov.justice.digital.hmpps.jobsboard.api.entity.SimplifiedPrisonLeaversJob
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversJobListPageDTO
import uk.gov.justice.digital.hmpps.jobsboard.api.resource.PrisonLeaversJobResourceController

@Configuration
class EmployerJobModelAssembler :
  RepresentationModelAssembler<SimplifiedPrisonLeaversJob, PrisonLeaversJobDTO> {
  override fun toModel(entity: SimplifiedPrisonLeaversJob): PrisonLeaversJobDTO {
    var prisonLeaversJobDTO: PrisonLeaversJobDTO = PrisonLeaversJobDTO(entity)
    entity.id?.let {
      prisonLeaversJobDTO.add(
        linkTo<PrisonLeaversJobResourceController> { getPrisonLeaversJob(it) }.withSelfRel(),
      )
    }
    return prisonLeaversJobDTO
  }

  fun toCollectionModelList(entities: Page<SimplifiedPrisonLeaversJob>): PrisonLeaversJobListPageDTO {
    var entityIterator = entities.content.iterator()
    val prisonLeaversJobDTOList = mutableListOf<PrisonLeaversJobDTO>()
    while (entityIterator?.hasNext() == true) {
      entityIterator?.next()?.let { prisonLeaversJobDTOList.add(toModel(it)) }
    }
    var prisonLeaversJobListPageDTO: PrisonLeaversJobListPageDTO = PrisonLeaversJobListPageDTO(prisonLeaversJobDTOList, entities.totalPages)
    return prisonLeaversJobListPageDTO
  }
}
