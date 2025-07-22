plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "8.3.3"
  kotlin("plugin.spring") version "2.2.0"
  kotlin("plugin.jpa") version "2.2.0"
  id("jvm-test-suite")
  id("jacoco")
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:5.4.9")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.data:spring-data-envers")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("org.postgresql:postgresql")

  developmentOnly("org.springframework.boot:spring-boot-devtools")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.mockk:mockk:1.13.3")
  testImplementation("org.springframework.boot:spring-boot-testcontainers") {
    testImplementation("org.apache.commons:commons-compress:1.27.1")
  }
  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("io.github.hakky54:logcaptor:2.11.0")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

testing {
  suites {
    @Suppress("UnstableApiUsage")
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()
    }

    @Suppress("UnstableApiUsage")
    val integrationTest by registering(JvmTestSuite::class) {
      useJUnitJupiter()
      dependencies {
        kotlin.target.compilations { named("integrationTest") { associateWith(getByName("main")) } }
        implementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
        implementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.2.1")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
        implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
        implementation("org.flywaydb:flyway-core")
        runtimeOnly("org.flywaydb:flyway-database-postgresql")
        implementation("com.h2database:h2")
        implementation("io.mockk:mockk:1.14.2")
        implementation("org.springframework.boot:spring-boot-testcontainers") {
          implementation("org.apache.commons:commons-compress:1.27.1")
        }
        implementation("org.testcontainers:postgresql")
        implementation("org.testcontainers:junit-jupiter")
        implementation("org.testcontainers:localstack")
        implementation("org.awaitility:awaitility-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-test-junit5")
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
