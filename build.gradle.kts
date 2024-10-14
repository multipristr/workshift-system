plugins {
    java
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.workshift"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
    implementation("org.slf4j:jcl-over-slf4j:1.7.36")
    implementation("org.slf4j:log4j-over-slf4j:1.7.36")
}

tasks {
    test {                                  
        useJUnitPlatform()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
