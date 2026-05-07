plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.2.3"
  kotlin("plugin.spring") version "2.3.21"
  kotlin("plugin.jpa") version "2.3.21"
  id("jvm-test-suite")
  id("jacoco")
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.2.0")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:7.3.1")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.data:spring-data-envers")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("org.postgresql:postgresql")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.2.0")
  testImplementation("uk.gov.justice.service.hmpps:hmpps-subject-access-request-lib:2.4.0")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.mockk:mockk:1.14.9")
  testImplementation("io.github.hakky54:logcaptor:2.12.6")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

testing {
  suites {
    @Suppress("UnstableApiUsage", "unused")
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()
    }

    @Suppress("UnstableApiUsage", "unused")
    val integrationTest by registering(JvmTestSuite::class) {
      useJUnitJupiter()
      dependencies {
        kotlin.target.compilations { named("integrationTest") { associateWith(getByName("main")) } }
        implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.2.0")
        implementation("uk.gov.justice.service.hmpps:hmpps-subject-access-request-test-support:2.4.0")
        implementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.mockito.kotlin:mockito-kotlin:6.3.0")
        implementation("org.wiremock:wiremock-standalone:3.13.2")
        implementation("org.flywaydb:flyway-core")
        runtimeOnly("org.flywaydb:flyway-database-postgresql")
        implementation("com.h2database:h2")
        implementation("io.mockk:mockk:1.14.9")
        implementation("org.springframework.boot:spring-boot-testcontainers")
        implementation("org.testcontainers:testcontainers-postgresql")
        implementation("org.testcontainers:testcontainers-junit-jupiter")
        implementation("org.testcontainers:testcontainers-localstack")
        implementation("org.awaitility:awaitility-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-test-junit5")
        implementation("io.swagger.parser.v3:swagger-parser:2.1.39") {
          exclude(group = "io.swagger.core.v3")
        }

        implementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
        implementation("org.springframework.boot:spring-boot-starter-webmvc-test")
        implementation("org.springframework.boot:spring-boot-webtestclient")
      }

      targets {
        all {
          testTask.configure {
            shouldRunAfter(tasks.named("test"))
          }
        }
      }
    }
  }
}

tasks {
  named("check") {
    dependsOn(named("test"), named("integrationTest"))
  }

  named("test") {
    finalizedBy("jacocoTestReport")
  }

  named("integrationTest") {
    mustRunAfter(named("test"))
  }

  named<JacocoReport>("jacocoTestReport") {
    reports {
      html.required.set(true)
      xml.required.set(true)
    }
  }

  named("assemble") {
    doFirst {
      delete(
        fileTree(project.layout.buildDirectory.get())
          .include("libs/*-plain.jar"),
      )
    }
  }
}

dependencyCheck {
  suppressionFiles.add("jobs-board-suppressions.xml")
}
