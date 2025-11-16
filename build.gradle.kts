plugins {
    id("application")
    java
}

group = "pro.savel.kafka"
version = "5.0.1"

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
    implementation("io.netty:netty-all:4.2.7.Final")
    implementation("org.apache.kafka:kafka-clients:4.1.1")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.25.2"))
    runtimeOnly("org.apache.logging.log4j:log4j-core")
    runtimeOnly("org.apache.logging.log4j:log4j-layout-template-json")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    testImplementation(platform("org.junit:junit-bom:5.14.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

distributions {
    main {
        contents {
            from("LICENSE")
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