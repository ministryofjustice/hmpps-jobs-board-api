package uk.gov.justice.digital.hmpps.hmppsjobsboardapi.assemblers

import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Page
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.entity.PrisonLeaversJob
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile.PrisonLeaversJobDTO
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.jsonprofile.PrisonLeaversJobListPageDTO
import uk.gov.justice.digital.hmpps.hmppsjobsboardapi.resource.PrisonLeaversJobResourceController

@Configuration
class EmployerJobModelAssembler :
  RepresentationModelAssembler<PrisonLeaversJob, PrisonLeaversJobDTO> {
  override fun toModel(entity: PrisonLeaversJob): PrisonLeaversJobDTO {
    var prisonLeaversJobDTO: PrisonLeaversJobDTO = PrisonLeaversJobDTO(entity)
    entity.id?.let {
      prisonLeaversJobDTO.add(
        linkTo<PrisonLeaversJobResourceController> { getPrisonLeaversJob(it) }.withSelfRel(),
      )
    }
    return prisonLeaversJobDTO
  }

  fun toCollectionModelList(entities: Page<PrisonLeaversJob>): PrisonLeaversJobListPageDTO {
    var entityIterator = entities.content.iterator()
    val prisonLeaversJobDTOList = mutableListOf<PrisonLeaversJobDTO>()
    while (entityIterator?.hasNext() == true) {
      entityIterator?.next()?.let { prisonLeaversJobDTOList.add(toModel(it)) }
    }
    var prisonLeaversJobListPageDTO: PrisonLeaversJobListPageDTO = PrisonLeaversJobListPageDTO(prisonLeaversJobDTOList, entities.totalPages, entities.pageable)
    return prisonLeaversJobListPageDTO
  }
}
