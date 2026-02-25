plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.0.3"
  kotlin("plugin.spring") version "2.3.0"
  kotlin("plugin.jpa") version "2.3.0"
  id("jvm-test-suite")
  id("jacoco")
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.0.0")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:6.0.1")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.data:spring-data-envers")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("org.postgresql:postgresql")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.0.0")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.mockk:mockk:1.14.5")
  testImplementation("io.github.hakky54:logcaptor:2.12.0")

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
        implementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.mockito.kotlin:mockito-kotlin:6.2.3")
        implementation("org.wiremock:wiremock-standalone:3.13.2")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
        implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
        implementation("org.flywaydb:flyway-core")
        runtimeOnly("org.flywaydb:flyway-database-postgresql")
        implementation("com.h2database:h2")
        implementation("io.mockk:mockk:1.14.5")
        implementation("org.springframework.boot:spring-boot-testcontainers")
        implementation("org.testcontainers:testcontainers-postgresql")
        implementation("org.testcontainers:testcontainers-junit-jupiter")
        implementation("org.testcontainers:testcontainers-localstack")
        implementation("org.awaitility:awaitility-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-test-junit5")

        implementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
        implementation("org.springframework.boot:spring-boot-starter-webmvc-test")
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
