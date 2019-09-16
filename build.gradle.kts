import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.3.50"
}

group = "com.bileto"
version = "0.1-SNAPSHOT"

dependencies {
	implementation(kotlin("stdlib"))

	compile("org.springframework:spring-web:5.1.9.RELEASE")

	compile ("org.slf4j:slf4j-api:1.7.28")

	compile ("com.fasterxml.jackson.core:jackson-core:2.9.9")
	compile ("com.fasterxml.jackson.core:jackson-annotations:2.9.9")
	compile ("com.fasterxml.jackson.core:jackson-databind:2.9.9")
	compile ("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
	compile ("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.9")

	testRuntime ("org.junit.platform:junit-platform-launcher:1.+")
	testRuntime ("org.junit.platform:junit-platform-runner:1.+")
	testCompile ("org.junit.jupiter:junit-jupiter-engine:5.+")
	testCompile ("org.junit.jupiter:junit-jupiter-params:5.+")

	testRuntime ("org.slf4j:slf4j-simple:1.7.28")
}

repositories {
	mavenCentral()
	jcenter()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}

val test: Test by tasks
test.useJUnitPlatform()