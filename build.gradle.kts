import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"
	kotlin("plugin.jpa") version "1.4.31"
	kotlin("kapt") version "1.4.32"
}

group = "com.vedblade"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.mapstruct:mapstruct:1.4.2.Final")
	kapt("org.mapstruct:mapstruct-processor:1.4.2.Final")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.11.0")
	testImplementation("com.squareup.okhttp3:okhttp:4.9.1")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
	testImplementation("io.kotest:kotest-runner-junit5:4.4.3")
	testImplementation("io.kotest:kotest-extensions-spring:4.4.3")
	testImplementation("io.kotest:kotest-assertions-core:4.4.3")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "15"
	}
}

tasks.test {
	useJUnitPlatform {
		val props = System.getProperties().map { it.key.toString() to it.value }.toMutableList()
		props += "kotest.tags" to "!integration"
		systemProperties = props.toMap()
	}
}

val integrationTest: Task = task<Test>("integrationTest") {
	useJUnitPlatform {
		val props = System.getProperties().map { it.key.toString() to it.value }.toMutableList()
		props += "kotest.tags" to "integration"
		systemProperties = props.toMap()
	}
	shouldRunAfter("test")
}

tasks.check { dependsOn(integrationTest) }