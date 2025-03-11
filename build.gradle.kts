plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "7.1.3"
  kotlin("plugin.spring") version "2.1.10"
  kotlin("plugin.jpa") version "2.1.10"
  id("jvm-test-suite")
  id("jacoco")
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:5.3.2")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.data:spring-data-envers")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5") {
    implementation("org.webjars:swagger-ui:5.20.0")
  }
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("org.postgresql:postgresql")

  developmentOnly("org.springframework.boot:spring-boot-devtools")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers") {
    testImplementation("org.apache.commons:commons-compress:1.27.1")
  }
  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("io.github.hakky54:logcaptor:2.10.1")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()
    }

    val integrationTest by registering(JvmTestSuite::class) {
      useJUnitJupiter()
      dependencies {
        testType.set(TestSuiteType.INTEGRATION_TEST)
        kotlin.target.compilations { named("integrationTest") { associateWith(getByName("main")) } }
        implementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
        implementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.2.0")
        implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
        implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
        implementation("org.flywaydb:flyway-core")
        runtimeOnly("org.flywaydb:flyway-database-postgresql")
        implementation("com.zaxxer:HikariCP:5.1.0")
        implementation("com.h2database:h2")
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
