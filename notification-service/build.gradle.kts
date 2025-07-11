plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
//	id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

group = "ru.tbank.itis"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://packages.confluent.io/maven/")
	}
}

dependencies {
	implementation("com.google.firebase:firebase-admin:9.2.0")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.kafka:spring-kafka:3.2.5")
//	implementation("org.apache.avro:avro:1.11.3")
//	implementation("io.confluent:kafka-avro-serializer:7.5.3")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
