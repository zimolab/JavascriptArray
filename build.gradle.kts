plugins {
    kotlin("jvm") version "1.5.10"
    java
    `maven-publish`
}

group = "com.zimolab"
version = "0.1.0-SNAPSHOT"

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}