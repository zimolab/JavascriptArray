plugins {
    application
}

group = "com.github.zimolab"
version = "0.1.1-SNAPSHOT"

val tornadofx_version = "1.7.20"

application {
    mainClassName = "com.zimolab.com.zimolab.jsarray.demo.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
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