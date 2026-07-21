plugins {
    id("application")
    java
}

group = "pro.savel.kafka"
version = "5.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "pro.savel.kafka.Application"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:4.2.15.Final")
    implementation("org.apache.kafka:kafka-clients:4.1.2")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.hibernate.validator:hibernate-validator:9.1.2.Final")
    implementation("org.slf4j:slf4j-api:2.0.18")
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.26.0"))
    runtimeOnly("org.apache.logging.log4j:log4j-core")
    runtimeOnly("org.apache.logging.log4j:log4j-layout-template-json")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.2")
    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

distributions {
    main {
        contents {
            from("LICENSE")
            from("NOTICE")
            from("kafka-proxy.service")
        }
    }
}

tasks.jar {
    manifest {
        attributes(mapOf("Implementation-Version" to version))
    }
}

tasks.test {
    useJUnitPlatform()
}
