import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.0.4")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.11")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets {
	create("integrationTest") {
		withConvention(KotlinSourceSet::class) {
			kotlin.srcDir("src/integrationTest/kotlin")
			compileClasspath += main.get().output + test.get().output
			runtimeClasspath += main.get().output + test.get().output
		}
		resources.srcDirs("src/integration-test/resources")
	}
}

configurations {
	getByName("integrationTestImplementation").extendsFrom(configurations.testImplementation.get())
	getByName("integrationTestRuntimeOnly").extendsFrom(configurations.testRuntimeOnly.get())
}

tasks.register<Test>("integrationTest") {
	description = "Runs the integration tests"
	group = "verification"

	dependsOn(
		subprojects.map {
			it.tasks.getByPath("integrationTest")
		}
	)
}