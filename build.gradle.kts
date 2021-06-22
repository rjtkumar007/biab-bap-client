import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.5.0"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
  kotlin("jvm") version "1.4.31"
  kotlin("plugin.spring") version "1.4.31"
  kotlin("kapt") version "1.4.31"
}

group = "org.beckn.one.sandbox"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

dependencies {
  val retrofitVersion = "2.9.+"

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.+")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.+")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")
  implementation("io.arrow-kt:arrow-optics:0.13.2")
  implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
  implementation("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
  implementation("com.squareup.retrofit2:retrofit-mock:$retrofitVersion")
  implementation("org.litote.kmongo:kmongo:4.2.7")

  kapt("io.arrow-kt:arrow-meta:0.13.2")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.kotest:kotest-runner-junit5:4.4.3")
  testImplementation("io.kotest:kotest-extensions-spring:4.4.3")
  testImplementation("com.github.tomakehurst:wiremock-jre8:2.28.0")
  testImplementation("org.testcontainers:mongodb:1.15.3")
  testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
