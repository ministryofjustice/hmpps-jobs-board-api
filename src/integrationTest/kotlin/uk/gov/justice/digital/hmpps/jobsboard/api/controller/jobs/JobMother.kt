package uk.gov.justice.digital.hmpps.jobsboard.api.controller.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JobMother {
  fun jobRequestBody(
    employerId: String,
    jobTitle: String,
    sector: String,
    industrySector: String,
    numberOfVacancies: Int,
    sourcePrimary: String,
    sourceSecondary: String? = null,
    charityName: String? = null,
    postCode: String,
    salaryFrom: Float,
    salaryTo: Float? = null,
    salaryPeriod: String,
    additionalSalaryInformation: String? = null,
    isPayingAtLeastNationalMinimumWage: Boolean,
    workPattern: String,
    hoursPerWeek: String,
    contractType: String,
    baseLocation: String? = null,
    essentialCriteria: String,
    desirableCriteria: String? = null,
    description: String,
    offenceExclusions: List<String>,
    isRollingOpportunity: Boolean,
    closingDate: String? = null,
    isOnlyForPrisonLeavers: Boolean,
    startDate: String? = null,
    howToApply: String,
    supportingDocumentationRequired: List<String>,
    supportingDocumentationDetails: String? = null,
  ): String {
    return jobBody(
      employerId,
      jobTitle,
      sector,
      industrySector,
      numberOfVacancies,
      sourcePrimary,
      sourceSecondary,
      charityName,
      postCode,
      salaryFrom,
      salaryTo,
      salaryPeriod,
      additionalSalaryInformation,
      isPayingAtLeastNationalMinimumWage,
      workPattern,
      hoursPerWeek,
      contractType,
      baseLocation,
      essentialCriteria,
      desirableCriteria,
      description,
      offenceExclusions,
      isRollingOpportunity,
      closingDate,
      isOnlyForPrisonLeavers,
      startDate,
      howToApply,
      supportingDocumentationRequired,
      supportingDocumentationDetails,
    )
  }

  fun newJobResponse(
    employerId: String,
    jobTitle: String,
    sector: String,
    industrySector: String,
    numberOfVacancies: Int,
    sourcePrimary: String,
    sourceSecondary: String? = null,
    charityName: String? = null,
    postCode: String,
    salaryFrom: Float,
    salaryTo: Float? = null,
    salaryPeriod: String,
    additionalSalaryInformation: String? = null,
    isPayingAtLeastNationalMinimumWage: Boolean,
    workPattern: String,
    hoursPerWeek: String,
    contractType: String,
    baseLocation: String? = null,
    essentialCriteria: String,
    desirableCriteria: String? = null,
    description: String,
    offenceExclusions: List<String>,
    isRollingOpportunity: Boolean,
    closingDate: String? = null,
    isOnlyForPrisonLeavers: Boolean,
    startDate: String? = null,
    howToApply: String,
    supportingDocumentationRequired: List<String>,
    supportingDocumentationDetails: String? = null,
    createdAt: String,
  ): String {
    return jobBody(
      employerId,
      jobTitle,
      sector,
      industrySector,
      numberOfVacancies,
      sourcePrimary,
      sourceSecondary,
      charityName,
      postCode,
      salaryFrom,
      salaryTo,
      salaryPeriod,
      additionalSalaryInformation,
      isPayingAtLeastNationalMinimumWage,
      workPattern,
      hoursPerWeek,
      contractType,
      baseLocation,
      essentialCriteria,
      desirableCriteria,
      description,
      offenceExclusions,
      isRollingOpportunity,
      closingDate,
      isOnlyForPrisonLeavers,
      startDate,
      howToApply,
      supportingDocumentationRequired,
      supportingDocumentationDetails,
      createdAt,
    )
  }

  fun newJobItemListResponse(
    employerId: String,
    employerName: String,
    jobTitle: String,
    numberOfVacancies: Int,
    sector: String,
    createdAt: String,
  ): String {
    return """
        {
          "employerId": "$employerId",
          "employerName": "$employerName",
          "jobTitle": "$jobTitle",
          "numberOfVacancies": $numberOfVacancies,
          "sector": "$sector",
          "createdAt": "$createdAt"
        }
    """.trimIndent()
  }

  private fun jobBody(
    employerId: String,
    jobTitle: String,
    sector: String,
    industrySector: String,
    numberOfVacancies: Int,
    sourcePrimary: String,
    sourceSecondary: String? = null,
    charityName: String? = null,
    postCode: String,
    salaryFrom: Float,
    salaryTo: Float? = null,
    salaryPeriod: String,
    additionalSalaryInformation: String? = null,
    isPayingAtLeastNationalMinimumWage: Boolean,
    workPattern: String,
    hoursPerWeek: String,
    contractType: String,
    baseLocation: String? = null,
    essentialCriteria: String,
    desirableCriteria: String? = null,
    description: String,
    offenceExclusions: List<String>,
    isRollingOpportunity: Boolean,
    closingDate: String? = null,
    isOnlyForPrisonLeavers: Boolean,
    startDate: String? = null,
    howToApply: String,
    supportingDocumentationRequired: List<String>,
    supportingDocumentationDetails: String? = null,
    createdAt: String? = null,
  ): String {
    val createdAtField = createdAt?.let { ",\n\"createdAt\": \"$it\"" } ?: ""
    return """
        {
          "employerId": "$employerId",
          "jobTitle": "$jobTitle",
          "sector": "$sector",
          "industrySector": "$industrySector",
          "numberOfVacancies": $numberOfVacancies,
          "sourcePrimary": "$sourcePrimary",
          "sourceSecondary": ${sourceSecondary?.asJson()},
          "charityName": ${charityName?.asJson()},
          "postCode": "$postCode",
          "salaryFrom": $salaryFrom,
          "salaryTo": $salaryTo,
          "salaryPeriod": "$salaryPeriod",
          "additionalSalaryInformation": ${additionalSalaryInformation?.asJson()},
          "isPayingAtLeastNationalMinimumWage": $isPayingAtLeastNationalMinimumWage,
          "workPattern": "$workPattern",
          "hoursPerWeek": "$hoursPerWeek",
          "contractType": "$contractType",
          "baseLocation": ${baseLocation?.asJson()},
          "essentialCriteria": "$essentialCriteria",
          "desirableCriteria": ${desirableCriteria?.asJson()},
          "description": ${description.asJson()},
          "offenceExclusions": ${offenceExclusions.asJson()},
          "isRollingOpportunity": $isRollingOpportunity,
          "closingDate": ${closingDate?.asJson()},
          "isOnlyForPrisonLeavers": $isOnlyForPrisonLeavers,
          "startDate": ${startDate?.asJson()},
          "howToApply": "$howToApply",
          "supportingDocumentationRequired": ${supportingDocumentationRequired.asJson()},
          "supportingDocumentationDetails": ${supportingDocumentationDetails?.asJson()}$createdAtField
        }
    """.trimIndent()
  }

  private fun String.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }

  private fun List<String>.asJson(): String {
    val mapper: ObjectMapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
  }
}
