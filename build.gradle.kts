import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "lunchtime"
version = "0.1.3-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.72" apply true
    id("com.github.node-gradle.node") version "2.2.4" apply true
    id("com.github.johnrengelman.shadow") version "5.0.0"
    application
}

repositories {
    jcenter()
    mavenCentral()
}

sourceSets {
    main {
        resources.srcDir("src/main/resources")
    }
}

tasks.register<NpmTask>("npmBuild") {
    setArgs(listOf("run", "build"))
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.3"
        apiVersion = "1.3"
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core:1.3.2")
    implementation("io.ktor:ktor-server-netty:1.3.2")
    implementation("io.ktor:ktor-jackson:1.3.2")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("io.ktor:ktor-server-test-host:1.3.2")
}

application {
    mainClassName = "org.ruslan.lunchtime.EntrypointKt"
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Main-Class" to application.mainClassName))
    }
}