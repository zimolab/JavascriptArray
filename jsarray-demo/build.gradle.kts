plugins {
    kotlin("jvm")
    application
}

group = "com.zimolab"
version = "0.1.0-SNAPSHOT"

val tornadofx_version = "1.7.20"

repositories {
    mavenCentral()
}

application {
    mainClassName = "com.zimolab.com.zimolab.jsarray.demo.MainKt"
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("no.tornado:tornadofx:$tornadofx_version")
    implementation(project(":jsarray-core-library"))

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}