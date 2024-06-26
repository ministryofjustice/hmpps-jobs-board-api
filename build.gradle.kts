import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "6.0.1"
  kotlin("plugin.spring") version "2.0.0"
  kotlin("plugin.jpa") version "2.0.0"
  id("jvm-test-suite")
  id("jacoco")
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:4.0.1")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
  runtimeOnly("org.flywaydb:flyway-database-postgresql")
  runtimeOnly("org.postgresql:postgresql")

  developmentOnly("org.springframework.boot:spring-boot-devtools")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("com.h2database:h2")
}

testing {
  suites {
    val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()
    }

    register<JvmTestSuite>("integrationTest") {
      dependencies {
        testType.set(TestSuiteType.INTEGRATION_TEST)
        kotlin.target.compilations { named("integrationTest") { associateWith(getByName("main")) } }
        implementation("org.springframework.boot:spring-boot-starter-test")
        implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
        implementation("io.jsonwebtoken:jjwt-jackson:0.12.5")
        runtimeOnly("org.flywaydb:flyway-database-postgresql")
        implementation("com.zaxxer:HikariCP:5.1.0")
        implementation("com.h2database:h2")
      }

      targets {
        all {
          testTask.configure {
            shouldRunAfter(test)
          }
        }
      }
    }
  }
}

tasks {
  withType<KotlinCompile> {
    named("check") {
      dependsOn(testing.suites.named("integrationTest"))
    }

    named("compileIntegrationTestKotlin") {
      dependsOn(named("copyAgent"))
    }

    named<JacocoReport>("jacocoTestReport") {
      dependsOn(named("check"))
      reports {
        html.required.set(true)
        xml.required.set(true)
      }
    }

    finalizedBy(named("jacocoTestReport"))
    named("assemble") {
      doFirst {
        delete(
          fileTree(project.layout.buildDirectory.get())
            .include("libs/*-plain.jar"),
        )
      }
    }
  }
}
