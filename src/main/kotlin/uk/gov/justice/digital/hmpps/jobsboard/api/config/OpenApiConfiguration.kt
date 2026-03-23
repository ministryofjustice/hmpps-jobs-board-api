package uk.gov.justice.digital.hmpps.jobsboard.api.config

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.justice.digital.hmpps.jobsboard.api.sar.data.SARContentDTO
import uk.gov.justice.hmpps.kotlin.sar.HmppsSubjectAccessRequestContent

@Configuration
class OpenApiConfiguration(buildProperties: BuildProperties) {
  private val version: String = buildProperties.version!!

  @Bean
  fun customOpenAPI(): OpenAPI = OpenAPI()
    .servers(
      listOf(
        Server().url("https://jobs-board-api-dev.hmpps.service.justice.gov.uk").description("Development"),
        Server().url("https://jobs-board-api-preprod.hmpps.service.justice.gov.uk").description("Pre-Production"),
        Server().url("https://jobs-board-api.hmpps.service.justice.gov.uk").description("Production"),
        Server().url("http://localhost:8080").description("Local"),
      ),
    )
    .tags(
      listOf(
        Tag().name("Popular"),
        Tag().name("Matching").description("Matching jobs API"),
        Tag().name("Jobs").description("Jobs API"),
        Tag().name("Employers").description("Employers API"),
        Tag().name("Applications").description("Job applications API"),
        Tag().name("EOI").description("Expression of Interest API"),
        Tag().name("Archived Jobs").description("Archived jobs API"),
        Tag().name("Dashboard").description("Reporting Dashboard API"),
      ),
    )
    .info(
      Info().title("HMPPS Jobs Board API").version(version)
        .contact(Contact().name("HMPPS Digital Studio").email("feedback@digital.justice.gov.uk")),
    )
    .components(
      Components()
        .addSecuritySchemes("view-jobs-board-role", SecurityScheme().addBearerJwtRequirement("ROLE_EDUCATION_WORK_PLAN_VIEW"))
        .addSecuritySchemes("edit-jobs-board-role", SecurityScheme().addBearerJwtRequirement("ROLE_EDUCATION_WORK_PLAN_EDIT"))
        .addSecuritySchemes("view-employers-role", SecurityScheme().addBearerJwtRequirement("ROLE_JOBS_BOARD__EMPLOYERS__RO"))
        .addSecuritySchemes("view-jobs-role", SecurityScheme().addBearerJwtRequirement("ROLE_JOBS_BOARD__JOBS__RO"))
        .addSecuritySchemes("edit-jobs-eoi-role", SecurityScheme().addBearerJwtRequirement("ROLE_JOBS_BOARD__JOBS__EOI__RW")),

    )
    .addSecurityItem(
      SecurityRequirement()
        .addList("view-jobs-board-role", listOf("read"))
        .addList("edit-jobs-board-role", listOf("read", "write"))
        .addList("view-employers-role", listOf("read"))
        .addList("view-jobs-role", listOf("read"))
        .addList("edit-jobs-eoi-role", listOf("read", "write")),
    )

  @Bean
  fun openAPICustomiser(): OpenApiCustomizer = OpenApiCustomizer {
    typedContentForSar(it)
  }

  private fun typedContentForSar(openApi: OpenAPI) {
    // register the SAR Content DTO
    val resolvedSchema = ModelConverters.getInstance().readAllAsResolvedSchema(SARContentDTO::class.java).also {
      openApi.components.addSchemas(it.schema.name, it.schema)
      it.referencedSchemas.forEach { openApi.components.addSchemas(it.key, it.value) }
    }
    // Touch up the SAR schema
    openApi.components.schemas[HmppsSubjectAccessRequestContent::class.simpleName]?.let { sarSchema ->
      sarSchema.properties["content"] = resolvedSchema.schema
      sarSchema.properties["attachments"]?.let {
        it.description = "(Not in use) ${it.description}"
        it.example = null
      }
    }
  }
}

private fun SecurityScheme.addBearerJwtRequirement(role: String): SecurityScheme = type(SecurityScheme.Type.HTTP)
  .scheme("bearer")
  .bearerFormat("JWT")
  .`in`(SecurityScheme.In.HEADER)
  .name("Authorization")
  .description("A HMPPS Auth access token with the `$role` role.")
