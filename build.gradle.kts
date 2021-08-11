plugins {
    kotlin("jvm") version "1.5.10"
    java
    `java-library`
    `maven-publish`
}

group = "com.github.zimolab"
version = "0.1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply {
        plugin("org.gradle.java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.gradle.maven-publish")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }
}