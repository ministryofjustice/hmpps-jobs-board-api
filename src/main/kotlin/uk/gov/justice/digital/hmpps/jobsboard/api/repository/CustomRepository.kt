package uk.gov.justice.digital.hmpps.jobsboard.api.repository

import uk.gov.justice.digital.hmpps.jobsboard.api.jsonprofile.PrisonLeaversSearchResultDTO

interface CustomRepository {
  fun findMatchingJobsbyClosingDate(prisonLeaversId: String, typeOfWorkList: List<String>, noOfRecords: Long): MutableList<PrisonLeaversSearchResultDTO>?
}
