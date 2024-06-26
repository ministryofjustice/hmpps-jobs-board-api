package uk.gov.justice.digital.hmpps.jobsboard.api.repository

import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PLIntrestedJobsClosingSoonDTO

interface CustomRepository {
  fun findIntrestedJobsbyClosingDate(prisonLeaversId: String): MutableList<PLIntrestedJobsClosingSoonDTO>?
}
