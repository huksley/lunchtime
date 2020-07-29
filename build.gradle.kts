import io.kotless.plugin.gradle.dsl.kotless
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "lunchtime"
version = "0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.72" apply true
    application
    id("io.kotless") version "0.1.5" apply true
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

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.3"
        apiVersion = "1.3"
        // Unlock experimental ktor features
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    //implementation("io.ktor:ktor-server-core:1.3.2")
    //implementation("io.ktor:ktor-server-netty:1.3.2")
    implementation("io.kotless:ktor-lang:0.1.5") {
        // Exclude log4j building
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

/*
application {
    mainClassName = "org.ruslan.lunchtime.EntrypointKt"
}
*/
