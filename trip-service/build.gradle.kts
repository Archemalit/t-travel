plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
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
}

dependencies {
//    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:1.33.0")
    implementation("org.liquibase:liquibase-core")
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")
    implementation ("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("com.nimbusds:nimbus-jose-jwt:9.37.3")
    implementation("com.google.firebase:firebase-admin:9.2.0")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.10" // актуальная версия
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // чтобы отчет создавался после тестов
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // чтобы сначала запускались тесты

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true) // читаемый HTML-отчет
    }

    classDirectories.setFrom(
            fileTree("${buildDir}/classes/java/main").exclude(
                    "ru/tbank/itis/tripbackend/mapper/**",
                    "ru/tbank/itis/tripbackend/security/**",
                    "ru/tbank/itis/tripbackend/handler/**"
            )
    )
}